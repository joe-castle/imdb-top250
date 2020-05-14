package co.uk.joecastle.imdbtop250.respository;

import co.uk.joecastle.imdbtop250.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);

}
