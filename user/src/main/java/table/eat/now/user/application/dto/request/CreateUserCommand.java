package table.eat.now.user.application.dto.request;

import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public record CreateUserCommand(String username,
                                String email,
                                String phone,
                                String password,
                                UserRole role) {

  public User toEntity() {
    return new User(
        username, email,
        phone, password, role
    );
  }

}
