package co.uk.joecastle.imdbtop250.controller;

import co.uk.joecastle.imdbtop250.model.UserModel;
import co.uk.joecastle.imdbtop250.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<UserModel> getUserAndWatchList() {
    UserModel userModel = userService.getUserModel();

    if (userModel != null) {
      return ResponseEntity.ok(userModel);
    } else {
      return ResponseEntity.noContent().build();
    }
  }
}
