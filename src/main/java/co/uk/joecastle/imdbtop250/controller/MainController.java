package co.uk.joecastle.imdbtop250.controller;

import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.model.Movie;
import co.uk.joecastle.imdbtop250.service.MovieService;
import co.uk.joecastle.imdbtop250.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class MainController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ModelAndView main() throws Exception {
        List<Movie> movies = movieService.getMovies();
        User user = userService.getUser();

        Map<String, Object> model = new HashMap<>(Map.of(
                "movies", movies,
                "genres", movieService.getAllGenres(movies)));

        if (user != null) {
            model.put("watchedList", user.getWatchedList());
        }

        return new ModelAndView("main", model);
    }

}
