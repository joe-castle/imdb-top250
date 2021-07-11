package co.uk.joecastle.imdbtop250.converter;

import co.uk.joecastle.imdbtop250.entity.MovieWatchList;
import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.model.MovieWatchListModel;
import co.uk.joecastle.imdbtop250.model.UserModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MovieWatchListEntityToMovieWatchListModel implements Converter<MovieWatchList, MovieWatchListModel> {

    @Override
    public MovieWatchListModel convert(MovieWatchList source) {
        return source == null
                ? null
                : MovieWatchListModel.builder()
                    .watchedList(source.getWatchedList())
                    .build();
    }

}
