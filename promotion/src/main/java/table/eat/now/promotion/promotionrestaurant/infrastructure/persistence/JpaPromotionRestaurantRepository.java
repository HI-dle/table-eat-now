package table.eat.now.promotion.promotionrestaurant.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotionrestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionrestaurant.domain.repository.PromotionRestaurantRepository;

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
