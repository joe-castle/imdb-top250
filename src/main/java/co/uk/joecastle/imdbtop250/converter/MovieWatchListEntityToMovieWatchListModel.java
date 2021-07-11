package co.uk.joecastle.imdbtop250.converter;

import co.uk.joecastle.imdbtop250.entity.MovieWatchList;
import co.uk.joecastle.imdbtop250.entity.Watched;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieWatchListEntityToMovieWatchListModel implements Converter<MovieWatchList, List<Watched>> {

    @Override
    public List<Watched> convert(MovieWatchList source) {
        return source == null
                ? null
                : source.getWatchedList();
    }

}
