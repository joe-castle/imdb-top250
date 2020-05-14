package co.uk.joecastle.imdbtop250.entity;

import co.uk.joecastle.imdbtop250.util.WatchedList;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    private String email;
    private WatchedList watchedList;

    public User(String email, WatchedList watchedList) {
        this.email = email;
        this.watchedList = watchedList;
    }

}
