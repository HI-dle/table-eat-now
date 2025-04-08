package table.eat.now.user.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.user.application.UserService;
import table.eat.now.user.presentation.dto.request.CreateUserRequest;
import table.eat.now.user.presentation.dto.response.CreateUserResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<CreateUserResponse> createUser(
      @RequestBody CreateUserRequest createUserRequest) {
    return ResponseEntity.ok(
        CreateUserResponse.from(
            userService.createUser(createUserRequest.toApplication())
        )
    );
  }
}
