package co.uk.joecastle.imdbtop250.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
//@Controller
public class MainController {

//    private final MovieService movieService;
//    private final UserService userService;
//
//    @Autowired
//    public MainController(MovieService movieService, UserService userService) {
//        this.movieService = movieService;
//        this.userService = userService;
//    }

    @GetMapping("/")
    public String main() throws Exception {
//        List<Movie> movies = movieService.getMovies();
//        User user = userService.getUser();
//
//        Map<String, Object> model = new HashMap<>(Map.of(
//                "movies", movies,
//                "genres", movieService.getAllGenres(movies)));
//
//        if (user != null) {
//            model.put("watchedList", user.getWatchedList());
//        }
//
//        return new ModelAndView("main", model);
        return "index";
    }

}
