package table.eat.now.promotion.promotionUser.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.domain.repository.PromotionUserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionUserRepository extends
    JpaRepository<PromotionUser, Long>, PromotionUserRepository {

  Optional<PromotionUser> findByPromotionUserUuidAndDeletedAtIsNull(
      String promotionUserUuid);
}
