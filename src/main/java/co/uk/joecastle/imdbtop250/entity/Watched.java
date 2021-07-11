package co.uk.joecastle.imdbtop250.entity;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Watched {

    @EqualsAndHashCode.Include
    private String title;

    private Boolean watched;

}
