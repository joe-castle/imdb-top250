package co.uk.joecastle.imdbtop250.entity;

import co.uk.joecastle.imdbtop250.util.WatchedList;
import lombok.*;
import org.springframework.data.annotation.Id;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    private String firstName;
    private String email;
    private WatchedList watchedList;

}
