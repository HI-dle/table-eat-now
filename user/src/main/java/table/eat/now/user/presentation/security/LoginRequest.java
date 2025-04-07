package table.eat.now.user.presentation.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  private String username;
  private String password;
}
