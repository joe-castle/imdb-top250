package co.uk.joecastle.imdbtop250.service;

import co.uk.joecastle.imdbtop250.converter.MovieWatchListEntityToMovieWatchListModel;
import co.uk.joecastle.imdbtop250.entity.MovieWatchList;
import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.entity.Watched;
import co.uk.joecastle.imdbtop250.exception.MovieScrapingException;
import co.uk.joecastle.imdbtop250.model.Movie;
import co.uk.joecastle.imdbtop250.respository.MovieWatchListRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private static final String IMDB_BASE_URL = "https://www.imdb.com";
    private static final String IMDB_TOP250_URI = IMDB_BASE_URL + "/chart/top/?sort=rank%2asc";
//    private static final List<String> ENHANCED_URIS = List.of("https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&view=advanced",
//            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=51&ref_=adv_nxt",
//            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=101&ref_=adv_nxt",
//            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=151&ref_=adv_nxt",
//            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=201&ref_=adv_nxt",
//            "https://www.imdb.com/search/title/?groups=top_250&sort=user_rating&start=251&ref_=adv_nxt");

    private final UserService userService;
    private final MovieWatchListRepository movieWatchListRepository;
    private final MovieWatchListEntityToMovieWatchListModel movieWatchListEntityToMovieWatchListModel;

    @Cacheable("movies")
    public List<Movie> getMovies() {
        try {
//            Map<String, Movie> enhancedMovies = getEnhancedMovies();

            String cssQuery = ".ipc-metadata-list li";

            Document document = Jsoup.connect(IMDB_TOP250_URI)
                    .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
                    .get();

            List<Movie> movies = document
                    .select(cssQuery)
                    .stream()
                    .map(this::getMovieInformation)
                    .collect(Collectors.toList());

//            if (MapUtils.isEmpty(enhancedMovies)) {
//                throw new MovieScrapingException("Unable to get top 250 movies from " + IMDB_TOP250_URI + " with CSS query " + cssQuery);
//            }

            if (CollectionUtils.isEmpty(movies)) {
                throw new MovieScrapingException("Unable to get top 250 movies from " + IMDB_TOP250_URI + " with CSS query " + cssQuery);
            }

            return movies;
        } catch (IOException e) {
            log.error("error getting titles with uri: {}", IMDB_TOP250_URI);
            throw new MovieScrapingException("error getting titles with uri: " + IMDB_TOP250_URI, e);
        }
    }

    @SneakyThrows
    private Movie getMovieInformation(Element element) {
        String titleAndPosition = element.select(".ipc-title__text").get(0).text();
        String position;
        String title;

        String titleAndPositionRegex = "(\\d{1,3}). (.+)";
        Matcher matcher = Pattern.compile(titleAndPositionRegex).matcher(titleAndPosition);

        if (matcher.find()) {
            position = matcher.group(1);
            title = matcher.group(2);
        } else {
            throw new MovieScrapingException("Unable to match title using regex: " + titleAndPositionRegex + " on title " + titleAndPosition);
        }

        log.debug("Getting movie info for {} at position {}", title, position);

        String movieUrl = IMDB_BASE_URL + element.select(".ipc-title-link-wrapper").attr("href");
        String cssQuery = "section[data-testid='hero-parent'] > div";
        Elements extraMovieInfo = Jsoup.connect(movieUrl)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
                .get()
                .select(cssQuery);

        if (CollectionUtils.isEmpty(extraMovieInfo)) {
            throw new MovieScrapingException("Unable to fetch movie data at url " + movieUrl + " with CSS query " + cssQuery);
        }

        // Data from TOP 250 page
        Movie movie = new Movie();
        movie.setPosition(Integer.parseInt(position));
        movie.setTitle(title);
        Elements metadata = element.select(".cli-title-metadata span");
        movie.setYear(Integer.parseInt(metadata.get(0).text()));
        movie.setTime(metadata.get(1).text());
        movie.setCertificate(metadata.get(2).text());
        movie.setRating(Double.parseDouble(element.select(".ipc-rating-star--rating").text()));
        movie.setPosterUrl(parsePosterUrl(element.select(".ipc-image").attr("srcset")));

        // Data from individual page
        Element details = extraMovieInfo.get(2);
        movie.setImdbUrl(movieUrl);
        movie.setDescription(details.select("span[data-testid='plot-xl']").text());
        movie.setGenre(details.select(".ipc-chip-list__scroller a span").stream().map(Element::text).collect(Collectors.joining(", ")));
        Elements people = details.select("li[data-testid='title-pc-principal-credit']");
        movie.setDirector(createPerson(people.get(0).select("a").get(0)));
        movie.setStars(people.get(2).select("div a").stream().map(this::createPerson).collect(Collectors.toList()));

        return movie;
    }

//    @Cacheable("enhancedMovies")
//    public Map<String, Movie> getEnhancedMovies() {
//        return ENHANCED_URIS
//                .stream()
//                .map(uri -> {
//                    try {
//                        return Jsoup.connect(uri)
//                                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
//                                .get()
//                                .select(".ipc-metadata-list li");
//                    } catch (IOException e) {
//                        log.error("error getting titles with uri {}", uri);
//                        throw new MovieScrapingException("error getting titles with uri: " + uri, e);
//                    }
//                })
//                .flatMap(elements -> elements.stream().map(element -> {
//                    Movie movie = new Movie();
//                    Element title = element.select(".ipc-title-link-wrapper").get(0);
//                    movie.setTitle(title.select("h3").get(0).text().replaceAll("\\d. ", ""));
//                    movie.setImdbUrl("https://www.imdb.com" + title.attr("href"));
//                    Elements metadata = element.select(".dli-title-metadata-item span");
//                    movie.setYear(Integer.parseInt(metadata.get(0).text()));
//                    movie.setTime(metadata.get(1).text());
////                    movie.setGenre(metadata.get(2).text());
////                    Elements content = element.select(".lister-item-content p");
//                    movie.setDescription(element.select(".ipc-html-content-inner-div").get(0).text());
//                    movie.setPosterUrl(parsePosterUrl(element.select(".ipc-image").get(0).attr("srcset")));
//                    movie.setCertificate(metadata.get(2).text());
////                    Elements people = content.select("a");
////                    movie.setDirector(new Movie.Person(people.get(0).text(), "https://www.imdb.com" + people.get(0).attr("href")));
////                    movie.setStars(people.subList(1, people.size()).stream().map(person ->
////                            new Movie.Person(person.text(), "https://www.imdb.com" + person.attr("href"))).collect(Collectors.toList()));
//                    return movie;
//                }))
//                .collect(Collectors.toMap(Movie::getTitle, Function.identity()));
//    }

    private String parsePosterUrl(String url) {
//        Matcher m = Pattern.compile("([XY])\\d{2}_CR(\\d),0,\\d{2},\\d{2}").matcher(url);
//        StringBuilder sb = new StringBuilder();
//
//        while (m.find()) {
//            m.appendReplacement(sb, m.group(1).equals("X")
//                    ? m.group(1) + "182_CR" + m.group(2) + ",0,182,268"
//                    : m.group(1) + "268_CR" + m.group(2) + ",0,182,268");
//        }
//
//        return m.appendTail(sb).toString();

        List<String> posterUrls = List.of(url.split(" \\d{1,3}w(, )?"));

        return posterUrls.get(posterUrls.size() - 1);
    }

    private Movie.Person createPerson(Element linkElement) {
        return new Movie.Person(linkElement.text(), linkElement.attr("href"));
    }

    public List<Watched> markFilmWatchedOrNotWatched(String movie) {
        try {
            User user = userService.getUser();
            MovieWatchList watchList = movieWatchListRepository.findByUserId(user.getId());

            if (watchList == null) {
                watchList = MovieWatchList.builder()
                        .userId(user.getId())
                        .watchedList(new ArrayList<>()).build();
            }

            Watched newWatched = Watched.builder().title(movie).watched(true).build();
            int index = watchList.getWatchedList().indexOf(newWatched);

            if (index > -1) {
                Watched amendWatched = watchList.getWatchedList().get(index);
                amendWatched.setWatched(!amendWatched.getWatched());
            } else {
                watchList.getWatchedList().add(newWatched);
            }

            return movieWatchListEntityToMovieWatchListModel.convert(movieWatchListRepository.save(watchList));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Watched> getMovieWatchListModel() {
        User user = userService.getUser();

        if (user != null) {
            return movieWatchListEntityToMovieWatchListModel
                    .convert(movieWatchListRepository.findByUserId(user.getId()));
        }

        return null;
    }

}
