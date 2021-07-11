package co.uk.joecastle.imdbtop250.service;

import co.uk.joecastle.imdbtop250.converter.UserEntityToUserModel;
import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.entity.Watched;
import co.uk.joecastle.imdbtop250.model.UserModel;
import co.uk.joecastle.imdbtop250.respository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
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

    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> saveUserOnAuthentication() {
        return event -> {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) event.getAuthentication().getPrincipal();

            User user = getUser(oAuth2User);

            if (user == null) {
                userRepository.save(User.builder()
                    .email(getEmail(Optional.of(oAuth2User)))
                    .name((String) oAuth2User.getAttributes().get("given_name"))
                    .build());
            }
        };
    }

    public UserModel getUserModel() {
        UserModel model = userEntityToUserModel.convert(getUser());
        Optional<DefaultOAuth2User> oAuth2USer = getOAuth2USer();

        if (model != null && oAuth2USer.isPresent()) {
            return model;
        }

        return null;
    }

//    public UserModel getUserAndWatchList() {
////        LookupOperation.newLookup()
////                .from("user")
//
//        LookupOperation aggregation = Aggregation
//                .lookup("movieWatchList", "userId", "id", "watchList");
////        UserModel model = userEntityToUserModel.convert(getUser());
////        Optional<DefaultOAuth2User> oAuth2USer = getOAuth2USer();
////
////        if (model != null && oAuth2USer.isPresent()) {
////            return model;
////        }
////
////        return null;
//    }

    public User getUser() {
        return userRepository.findByEmail(getEmail());
    }

    private User getUser(DefaultOAuth2User oAuth2User) {
        return userRepository.findByEmail(getEmail(Optional.of(oAuth2User)));
    }

    private String getEmail() {
        return getEmail(getOAuth2USer());
    }

    private String getEmail(Optional<DefaultOAuth2User> oAuth2User) {
        return oAuth2User
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
