package table.eat.now.user.application.security.dto;

import table.eat.now.user.domain.entity.UserRole;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public interface Authenticated {
  Long getId();
  String getUsername();
  String getPassword();
  UserRole getRole();
}
