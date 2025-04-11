package table.eat.now.promotion.promotion.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionRepositoryRepository extends JpaRepository<Promotion, Long>,
    PromotionRepository, JpaPromotionRepositoryCustom {

  Optional<Promotion> findByPromotionUuidAndDeletedByIsNull(String promotionUuid);

}
