package table.eat.now.user.application.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import table.eat.now.user.application.security.dto.CustomUserDetails;
import table.eat.now.user.application.security.dto.UserDetailsDto;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.repository.UserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("없는 사용자 입니다."));
    return new CustomUserDetails(UserDetailsDto.from(user));
  }

}
