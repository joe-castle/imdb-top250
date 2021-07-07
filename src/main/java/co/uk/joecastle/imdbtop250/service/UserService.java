package co.uk.joecastle.imdbtop250.service;

import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.entity.Watched;
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

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean updateMovie(String movie) {
        try {
            User user = getUser();

            if (user == null) {
                String email = getEmail();

                userRepository.save(User
                        .builder()
                        .email(email)
                        .watchedList(new WatchedList(List.of(Watched.builder().title(movie).watched(true).build())))
                        .build());
            } else {
                Watched newWatched = Watched.builder().title(movie).watched(true).build();
                int index = user.getWatchedList().indexOf(newWatched);

                if (index > -1) {
                    Watched amendWatched = user.getWatchedList().get(index);
                    amendWatched.setWatched(!amendWatched.getWatched());
                } else {
                    user.getWatchedList().add(newWatched);
                }

                userRepository.save(user);
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public User getUser() {
        return userRepository.findByEmail(getEmail());
    }

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (String) ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes().get("email");
        }

        return null;
    }

}
