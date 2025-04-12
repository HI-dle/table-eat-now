package table.eat.now.promotion.promotion.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.promotion.promotion.application.client.PromotionClient;
import table.eat.now.promotion.promotion.application.dto.client.response.GetPromotionRestaurantInfo;
import table.eat.now.promotion.promotion.infrastructure.client.feign.PromotionRestaurantFeignClient;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@Component
@RequiredArgsConstructor
public class PromotionRestaurantClientImpl implements PromotionClient {

  private final PromotionRestaurantFeignClient promotionRestaurantFeignClient;
  @Override
  public GetPromotionRestaurantInfo findRestaurantsByPromotions(String restaurantUuid) {

    return promotionRestaurantFeignClient.findRestaurantsByPromotions(restaurantUuid);
  }
}
