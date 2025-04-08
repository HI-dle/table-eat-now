package table.eat.now.user.presentation.dto.response;

import lombok.Builder;
import table.eat.now.user.application.dto.response.CreateUserInfo;
import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Builder
public record CreateUserResponse(Long userId,
                                 String username,
                                 String email,
                                 String phone,
                                 String password,
                                 UserRole role
                               ) {

  public static CreateUserResponse from(CreateUserInfo responseCommand) {
    return CreateUserResponse.builder()
        .userId(responseCommand.userId())
        .username(responseCommand.username())
        .email(responseCommand.email())
        .phone(responseCommand.phone())
        .password(responseCommand.password())
        .role(responseCommand.role())
        .build();
  }
}
