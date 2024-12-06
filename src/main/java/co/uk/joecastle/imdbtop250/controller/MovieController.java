package co.uk.joecastle.imdbtop250.controller;

import co.uk.joecastle.imdbtop250.entity.Watched;
import co.uk.joecastle.imdbtop250.model.Movie;
import co.uk.joecastle.imdbtop250.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/watchList")
    public List<Watched> getMovieWatchList() {
        return movieService.getMovieWatchListModel();
    }

    @PostMapping(value = "/{movie}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Watched> markFilmWatchedOrNotWatched(@PathVariable String movie) {
        return movieService.markFilmWatchedOrNotWatched(movie);
    }

}
