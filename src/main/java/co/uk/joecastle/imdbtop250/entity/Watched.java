package co.uk.joecastle.imdbtop250.entity;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Watched {

    private String title;
    private Boolean watched;

}
