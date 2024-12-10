package co.uk.joecastle.imdbtop250.service;

import co.uk.joecastle.imdbtop250.converter.MovieWatchListEntityToMovieWatchListModel;
import co.uk.joecastle.imdbtop250.entity.MovieWatchList;
import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.entity.Watched;
import co.uk.joecastle.imdbtop250.exception.MovieScrapingException;
import co.uk.joecastle.imdbtop250.model.Movie;
import co.uk.joecastle.imdbtop250.respository.MovieWatchListRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

  private static final String IMDB_BASE_URL = "https://www.imdb.com";
  private static final String IMDB_TOP250_URI = IMDB_BASE_URL + "/chart/top/?sort=rank%2asc";

  private final UserService userService;
  private final MovieWatchListRepository movieWatchListRepository;
  private final MovieWatchListEntityToMovieWatchListModel movieWatchListEntityToMovieWatchListModel;
  private final ObjectMapper mapper;

  @Cacheable("moviesCache")
  public List<Movie> getMovies() {
    try {
      String cssQuery = "script[type='application/ld+json']";

      List<Movie> movies =
          Jsoup.connect(IMDB_TOP250_URI)
              .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
              .get()
              .select(cssQuery)
              .stream()
              .flatMap(this::getMovieInformation)
              .collect(Collectors.toList());

      if (CollectionUtils.isEmpty(movies)) {
        throw new MovieScrapingException(
            "Unable to get top 250 movies from " + IMDB_TOP250_URI + " with CSS query " + cssQuery);
      }

      return movies;
    } catch (IOException e) {
      throw new MovieScrapingException("error getting titles with uri: " + IMDB_TOP250_URI, e);
    }
  }

  private Stream<Movie> getMovieInformation(Element element) {
    try {
      return StreamSupport.stream(
              mapper.readTree(element.data()).get("itemListElement").spliterator(), false)
          .map(jsonNode -> jsonNode.get("item"))
          .map(
              jsonNode ->
                  Movie.builder()
                      .title(jsonNode.get("name").textValue())
                      .certificate(
                          Optional.ofNullable(jsonNode.get("contentRating"))
                              .map(JsonNode::textValue)
                              .orElse("X"))
                      .rating(jsonNode.at("/aggregateRating/ratingValue").doubleValue())
                      .posterUrl(jsonNode.get("image").textValue())
                      .genre(jsonNode.get("genre").textValue())
                      .description(jsonNode.get("description").textValue())
                      .imdbUrl(jsonNode.get("url").textValue())
                      .build())
          .map(
              movie -> {
                try {
                  log.debug("Getting movie info for {}", movie.getTitle());

                  String data =
                      Jsoup.connect(movie.getImdbUrl())
                          .header(HttpHeaders.ACCEPT_LANGUAGE, "en-GB")
                          .get()
                          .select("script[id='__NEXT_DATA__']")
                          .get(0)
                          .data();

                  JsonNode pageJson = mapper.readTree(data);

                  movie.setYear(
                      pageJson.at("/props/pageProps/aboveTheFoldData/releaseYear/year").intValue());
                  movie.setTime(
                      pageJson
                          .at(
                              "/props/pageProps/aboveTheFoldData/runtime/displayableProperty/value/plainText")
                          .textValue());
                  JsonNode credits =
                      pageJson.at("/props/pageProps/aboveTheFoldData/principalCredits");
                  getPersonStream(credits, "Directors?").findFirst().ifPresent(movie::setDirector);

                  movie.setStars(getPersonStream(credits, "Stars").collect(Collectors.toList()));

                  return movie;
                } catch (IOException e) {
                  throw new MovieScrapingException(
                      "Error fetching movie data with url: " + movie.getImdbUrl(), e);
                }
              });
    } catch (JsonProcessingException e) {
      throw new MovieScrapingException("error parsing json: " + element.text(), e);
    }
  }

  private static Stream<Movie.Person> getPersonStream(JsonNode credits, String personTypeRegex) {
    return StreamSupport.stream(credits.spliterator(), false)
        .filter(jsonNode -> jsonNode.at("/category/text").textValue().matches(personTypeRegex))
        .flatMap(jsonNode -> StreamSupport.stream(jsonNode.get("credits").spliterator(), false))
        .map(
            jsonNode ->
                Movie.Person.builder()
                    .name(jsonNode.at("/name/nameText/text").textValue())
                    .url(IMDB_BASE_URL + "/name/" + jsonNode.at("/name/id").textValue())
                    .build());
  }

  public List<Watched> markFilmWatchedOrNotWatched(String movie) {
    try {
      User user = userService.getUser();
      MovieWatchList watchList = movieWatchListRepository.findByUserId(user.getId());

      if (watchList == null) {
        watchList =
            MovieWatchList.builder().userId(user.getId()).watchedList(new ArrayList<>()).build();
      }

      Watched newWatched = Watched.builder().title(movie).watched(true).build();
      int index = watchList.getWatchedList().indexOf(newWatched);

      if (index > -1) {
        Watched amendWatched = watchList.getWatchedList().get(index);
        amendWatched.setWatched(!amendWatched.getWatched());
      } else {
        watchList.getWatchedList().add(newWatched);
      }

      return movieWatchListEntityToMovieWatchListModel.convert(
          movieWatchListRepository.save(watchList));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public List<Watched> getMovieWatchListModel() {
    User user = userService.getUser();

    if (user != null) {
      return movieWatchListEntityToMovieWatchListModel.convert(
          movieWatchListRepository.findByUserId(user.getId()));
    }

    return null;
  }
}
