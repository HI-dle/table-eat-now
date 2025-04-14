package table.eat.now.promotion.promotion.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.promotion.promotion.application.dto.client.response.GetPromotionRestaurantInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */

@FeignClient(name = "promotion")
public interface PromotionRestaurantFeignClient {
  @GetMapping("/internal/v1/promotion-restaurants/{restaurantUuid}/promotion/{promotionUuid}")
  GetPromotionRestaurantInfo findRestaurantsByPromotions(
      @PathVariable("restaurantUuid") String restaurantUuid,
      @PathVariable("promotionUuid") String promotionUuid);
}
