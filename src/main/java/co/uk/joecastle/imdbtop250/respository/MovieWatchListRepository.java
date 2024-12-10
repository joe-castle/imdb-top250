package co.uk.joecastle.imdbtop250.respository;

import co.uk.joecastle.imdbtop250.entity.MovieWatchList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieWatchListRepository extends MongoRepository<MovieWatchList, String> {

  MovieWatchList findByUserId(String userId);
}
