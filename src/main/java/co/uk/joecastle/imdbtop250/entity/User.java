package co.uk.joecastle.imdbtop250.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id private String id;

  private String name;
  private String email;
}
