package table.eat.now.user.application.security.dto;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final transient UserDetailsDto userDetailsDto;

  public UserRole getRole() {
    return this.userDetailsDto.role();
  }
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userDetailsDto.getAuthorities();
  }

  public Long getMemberId() {
    return userDetailsDto.id();
  }

  @Override
  public String getPassword() {
    return userDetailsDto.password();
  }

  @Override
  public String getUsername() {
    return userDetailsDto.username();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
