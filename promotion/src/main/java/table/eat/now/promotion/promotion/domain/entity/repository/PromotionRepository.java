package table.eat.now.promotion.promotion.domain.entity.repository;

import java.util.Optional;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionRepository {

  Promotion save(Promotion promotion);
  Optional<Promotion> findByPromotionUuidAndDeletedByIsNull(String promotionUuid);
}
