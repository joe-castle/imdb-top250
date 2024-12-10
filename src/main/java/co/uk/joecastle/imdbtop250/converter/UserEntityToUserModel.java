package co.uk.joecastle.imdbtop250.converter;

import co.uk.joecastle.imdbtop250.entity.User;
import co.uk.joecastle.imdbtop250.model.UserModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserModel implements Converter<User, UserModel> {

  @Override
  public UserModel convert(User source) {
    return source != null ? UserModel.builder().name(source.getName()).build() : null;
  }
}
