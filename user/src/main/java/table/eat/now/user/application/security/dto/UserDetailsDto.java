package table.eat.now.user.application.security.dto;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public record UserDetailsDto(
    Long id,

    String username,

    String password,

    UserRole role

) {

  public static UserDetailsDto from(User user) {
    return new UserDetailsDto(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getRole()
    );
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role.name()));
    return authorities;
  }
}
