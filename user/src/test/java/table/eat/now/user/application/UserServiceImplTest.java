package table.eat.now.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import table.eat.now.user.application.dto.request.CreateUserCommand;
import table.eat.now.user.application.dto.response.CreateUserInfo;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.entity.UserRole;
import table.eat.now.user.domain.repository.UserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void createUser() {

    // given
    CreateUserCommand command = new CreateUserCommand(
        "testUser",
        "test@email.com",
        "1234",
        "01012345678",
        UserRole.CUSTOMER
    );

    User user = command.toEntity();

    String encodedPassword = "encoded1234";
    when(passwordEncoder.encode("01012345678"))
        .thenReturn(encodedPassword);


    user.encryptPassword(encodedPassword);

    when(userRepository.save(any(User.class)))
        .thenReturn(user);

    // when
    CreateUserInfo result = userService.createUser(command);

    // then
    assertThat(result.username()).isEqualTo("testUser");
    assertThat(result.email()).isEqualTo("test@email.com");
    assertThat(result.password()).isEqualTo("encoded1234");

    verify(userRepository).save(any(User.class));
  }
}
