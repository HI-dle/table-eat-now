package table.eat.now.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import table.eat.now.user.application.dto.request.CreateUserCommand;
import table.eat.now.user.application.dto.response.CreateUserInfo;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.repository.UserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public CreateUserInfo createUser(CreateUserCommand createUserCommand) {
    User user = createUserCommand.toEntity(createUserCommand);
    user.encryptPassword(passwordEncoder.encode(user.getPassword()));
    log.info("패스워드 {}", user.getPassword());

    return CreateUserInfo.from(
        userRepository.save(user));
  }
}
