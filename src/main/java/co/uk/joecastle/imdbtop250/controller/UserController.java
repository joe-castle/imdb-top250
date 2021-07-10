package co.uk.joecastle.imdbtop250.controller;

import co.uk.joecastle.imdbtop250.model.UserModel;
import co.uk.joecastle.imdbtop250.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserModel> getUser() {
        UserModel userModel = userService.getUserModel();

        if (userModel != null) {
            return ResponseEntity.ok(userModel);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/movie/{movie}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserModel makeFilmWatchedOrNotWatched(@PathVariable String movie) {
        return userService.makeFilmWatchedOrNotWatched(movie);
    }

}
