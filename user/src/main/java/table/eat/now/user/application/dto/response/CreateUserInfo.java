package table.eat.now.user.application.dto.response;

import lombok.Builder;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Builder
public record CreateUserInfo(Long userId,
                             String username,
                             String email,
                             String phone,
                             String password,
                             UserRole role) {

  public static CreateUserInfo from(User user) {
    return CreateUserInfo.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .phone(user.getPhone())
        .password(user.getPassword())
        .role(user.getRole())
        .build();
  }
}
