package co.uk.joecastle.imdbtop250.controller;

import co.uk.joecastle.imdbtop250.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("movies")
public class MovieController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/{movie}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveMovies(@PathVariable String movie) {
        return userService.updateMovie(movie);
    }

}
