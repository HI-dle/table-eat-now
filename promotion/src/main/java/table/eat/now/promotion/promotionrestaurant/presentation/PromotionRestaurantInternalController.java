package table.eat.now.promotion.promotionrestaurant.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.promotion.promotionrestaurant.application.service.PromotionRestaurantService;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.response.GetPromotionRestaurantResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@RestController
@RequestMapping("/internal/v1/promotion-restaurants")
@RequiredArgsConstructor
public class PromotionRestaurantInternalController {

  private final PromotionRestaurantService promotionRestaurantService;

  @GetMapping("/{restaurantUuid}")
  GetPromotionRestaurantResponse findRestaurantsByPromotions(@PathVariable String restaurantUuid) {
    return GetPromotionRestaurantResponse.from(
        promotionRestaurantService.findRestaurantsByPromotions(restaurantUuid));
  }
}
