package co.uk.joecastle.imdbtop250.service;

import co.uk.joecastle.imdbtop250.converter.UserEntityToUserModel;
import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.entity.Watched;
import co.uk.joecastle.imdbtop250.model.UserModel;
import co.uk.joecastle.imdbtop250.respository.UserRepository;
import co.uk.joecastle.imdbtop250.util.WatchedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityToUserModel userEntityToUserModel;

    @Autowired
    public UserService(UserRepository userRepository, UserEntityToUserModel userEntityToUserModel) {
        this.userRepository = userRepository;
        this.userEntityToUserModel = userEntityToUserModel;
    }

    public UserModel makeFilmWatchedOrNotWatched(String movie) {
        try {
            User user = getUser();

            if (user == null) {
                return userEntityToUserModel.convert(userRepository.save(User
                        .builder()
                        .email(getEmail())
                        .watchedList(new WatchedList(List.of(Watched.builder().title(movie).watched(true).build())))
                        .build()));
            } else {
                Watched newWatched = Watched.builder().title(movie).watched(true).build();
                int index = user.getWatchedList().indexOf(newWatched);

                if (index > -1) {
                    Watched amendWatched = user.getWatchedList().get(index);
                    amendWatched.setWatched(!amendWatched.getWatched());
                } else {
                    user.getWatchedList().add(newWatched);
                }

                return userEntityToUserModel.convert(userRepository.save(user));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public UserModel getUserModel() {
        UserModel model = userEntityToUserModel.convert(getUser());
        Optional<DefaultOAuth2User> oAuth2USer = getOAuth2USer();

        if (model != null && oAuth2USer.isPresent()) {
            model.setName((String) oAuth2USer.get().getAttributes().get("given_name"));
            return model;
        }

        return null;
    }

    private User getUser() {
        return userRepository.findByEmail(getEmail());
    }

    private String getEmail() {
        return getOAuth2USer()
                .map(defaultOAuth2User -> (String) defaultOAuth2User.getAttributes().get("email"))
                .orElse(null);
    }

    private Optional<DefaultOAuth2User> getOAuth2USer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return Optional.ofNullable((DefaultOAuth2User) authentication.getPrincipal());
        }

        return Optional.empty();
    }

}
