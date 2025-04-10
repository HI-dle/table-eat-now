package table.eat.now.promotion.promotionRestaurant.infrastructure.persistence;

import java.util.Optional;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.domain.repository.PromotionRestaurantRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionRestaurantRepository extends
    JpaRepository<PromotionRestaurant, Long>, PromotionRestaurantRepository,
    JpaPromotionRestaurantRepositoryCustom{
  Optional<PromotionRestaurant> findByPromotionRestaurantUuidAndDeletedAtIsNull(
      String promotionRestaurantUuid);

  Optional<PromotionRestaurant> findByRestaurantUuidAAndDeletedAtIsNull(
      String restaurantUuid);
}
