package table.eat.now.promotion.promotionRestaurant.domain.repository;

import java.util.Optional;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionRestaurantRepository {

  PromotionRestaurant save(PromotionRestaurant promotionRestaurant);
  Optional<PromotionRestaurant> findByPromotionRestaurantUuidAndDeletedAtIsNull(
      String promotionRestaurantUuid);
}
