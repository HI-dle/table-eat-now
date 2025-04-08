package table.eat.now.user.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.user.application.UserService;
import table.eat.now.user.application.dto.request.CreateUserCommand;
import table.eat.now.user.application.dto.response.CreateUserInfo;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.entity.UserRole;
import table.eat.now.user.presentation.security.jwt.TokenProvider;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @TestConfiguration
  static class TestSecurityConfig {
    @Bean
    public TokenProvider tokenProvider() {
      return mock(TokenProvider.class);
    }
  }

  @DisplayName("회원가입")
  @Test
  void 회원가입() throws Exception {
      // given
    CreateUserCommand createUserCommand = new CreateUserCommand(
        "test", "test", "123",
        "213", UserRole.CUSTOMER);
    String encodePassword =
        passwordEncoder.encode(createUserCommand.password());

    User user = new User("test", "test", "123",
        encodePassword, UserRole.CUSTOMER);
      // when
    when(userService.createUser(createUserCommand))
        .thenReturn(CreateUserInfo.from(user));

    mockMvc.perform(post("/api/v1/users/signup")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(createUserCommand)))
        .andExpect(status().isOk());


    // then
    assertThat(createUserCommand.username())
        .isEqualTo(user.getUsername());
  }

}