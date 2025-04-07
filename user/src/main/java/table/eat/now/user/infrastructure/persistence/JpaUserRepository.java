package table.eat.now.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.user.domain.entity.User;
import table.eat.now.user.domain.repository.UserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

}
