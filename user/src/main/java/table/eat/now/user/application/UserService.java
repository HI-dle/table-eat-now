package table.eat.now.user.application;

import table.eat.now.user.application.dto.request.CreateUserCommand;
import table.eat.now.user.application.dto.response.CreateUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public interface UserService {

  CreateUserInfo createUser(CreateUserCommand createUserCommand);

}
