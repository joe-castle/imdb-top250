package co.uk.joecastle.imdbtop250.entity;

import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieWatchList {

  @Id private String id;

  private String userId;
  private List<Watched> watchedList;
}
