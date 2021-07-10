package co.uk.joecastle.imdbtop250.model;

import co.uk.joecastle.imdbtop250.entity.Watched;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {

    private String name;
    private List<Watched> watchedList;

}
