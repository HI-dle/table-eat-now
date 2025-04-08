package table.eat.now.promotionRestaurant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotionRestaurant.domain.repository.PromotionRestaurantRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionRestaurantRepository extends
    JpaRepository<PromotionRestaurant, Long>, PromotionRestaurantRepository {

}
