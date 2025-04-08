package table.eat.now.user.presentation.dto.request;

import table.eat.now.user.application.dto.request.CreateUserCommand;
import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public record CreateUserRequest(String username,
                                String email,
                                String phone,
                                String password,
                                UserRole role
                               ) {

  public CreateUserCommand toApplication() {
    return new CreateUserCommand(
        username, email,
        phone, password, role
    );
  }
}
