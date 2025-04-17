package table.eat.now.promotion.promotionuser.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionuser.domain.repository.PromotionUserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionUserRepository extends
    JpaRepository<PromotionUser, Long>, PromotionUserRepository, JpaPromotionUserRepositoryCustom{

  Optional<PromotionUser> findByPromotionUserUuidAndDeletedAtIsNull(
      String promotionUserUuid);
  Optional<PromotionUser> findByUserIdAndDeletedAtIsNull(Long userId);


}
