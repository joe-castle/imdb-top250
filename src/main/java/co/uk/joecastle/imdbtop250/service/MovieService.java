package co.uk.joecastle.imdbtop250.service;

import co.uk.joecastle.imdbtop250.model.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private static final String IMDB_TOP250_URI = "https://www.imdb.com/chart/top/";
    private static final List<String> ENHANCED_URIS = List.of("https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&view=advanced",
            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=51&ref_=adv_nxt",
            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=101&ref_=adv_nxt",
            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=151&ref_=adv_nxt",
            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=201&ref_=adv_nxt",
            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=251&ref_=adv_nxt");

    @Cacheable("movies")
    public List<Movie> getMovies() {
        try {
            Map<String, Movie> enhancedMovies = getEnhancedMovies();

            return Jsoup.connect(IMDB_TOP250_URI)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
                    .get()
                    .select(".lister-list tr")
                    .stream()
                    .map(element -> {
                        Movie movie = enhancedMovies.get(element.select(".titleColumn a").get(0).text());
                        movie.setPosition(Integer.parseInt(element.select(".titleColumn").get(0).text().replaceAll("\\..++", "")));
                        movie.setRating(Double.parseDouble(element.select(".imdbRating strong").get(0).text()));
                        return movie;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error getting titles with uri: {uri}");
        }
    }

    @Cacheable("enhancedMovies")
    public Map<String, Movie> getEnhancedMovies() {
        return ENHANCED_URIS
                .stream()
                .map(uri -> {
                    try {
                        return Jsoup.connect(uri)
                                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
                                .get()
                                .select(".lister-item");
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("error getting titles with uri: {uri}");
                    }
                })
                .flatMap(elements -> elements.stream().map(element -> {
                    Movie movie = new Movie();
                    Element title = element.select(".lister-item-header a").get(0);
                    movie.setTitle(title.text());
                    movie.setImdbUrl("https://www.imdb.com" + title.attr("href"));
                    movie.setYear(Integer.parseInt(element.select(".lister-item-year").get(0).text().replaceAll(".+(\\d{4}).+", "$1")));
                    Elements content = element.select(".lister-item-content p");
                    movie.setDescription(content.get(1).text());
                    movie.setPosterUrl(parsePosterUrl(element.select(".lister-item-image img").get(0).attr("loadlate")));
                    movie.setCertificate(element.select(".certificate").isEmpty() ? "X" : element.select(".certificate").get(0).text());
                    movie.setTime(element.select(".runtime").get(0).text());
                    movie.setGenre(element.select(".genre").get(0).text());
                    Elements people = content.select("a");
                    movie.setDirector(new Movie.Person(people.get(0).text(), "https://www.imdb.com" + people.get(0).attr("href")));
                    movie.setStars(people.subList(1, people.size()).stream().map(person ->
                            new Movie.Person(person.text(), "https://www.imdb.com" + person.attr("href"))).collect(Collectors.toList()));
                    return movie;
                }))
                .collect(Collectors.toMap(Movie::getTitle, Function.identity()));
    }

    private String parsePosterUrl(String url) {
        Matcher m = Pattern.compile("([XY])\\d{2}_CR(\\d),0,\\d{2},\\d{2}").matcher(url);
        StringBuilder sb = new StringBuilder();

        while (m.find()) {
            m.appendReplacement(sb, m.group(1).equals("X")
                    ? m.group(1) + "182_CR" + m.group(2) + ",0,182,268"
                    : m.group(1) + "268_CR" + m.group(2) + ",0,182,268");
        }

        return m.appendTail(sb).toString();
    }

}
