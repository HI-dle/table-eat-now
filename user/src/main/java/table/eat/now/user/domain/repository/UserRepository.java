package table.eat.now.user.domain.repository;

import java.util.Optional;
import table.eat.now.user.domain.entity.User;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public interface UserRepository {

  Optional<User> findById(Long userId);

  Optional<User> findByUsername(String username);

  User save(User user);
}
