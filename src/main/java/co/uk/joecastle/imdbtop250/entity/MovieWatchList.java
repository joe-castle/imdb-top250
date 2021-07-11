package co.uk.joecastle.imdbtop250.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieWatchList {

    @Id
    private String id;

    private String userId;
    private List<Watched> watchedList;

}
