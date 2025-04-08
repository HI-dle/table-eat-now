package table.eat.now.promotion.promotionRestaurant.presentation.dto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.promotion.promotionRestaurant.application.service.PromotionRestaurantService;
import table.eat.now.promotion.promotionRestaurant.presentation.dto.request.CreatePromotionRestaurantRequest;
import table.eat.now.promotion.promotionRestaurant.presentation.dto.response.CreatePromotionRestaurantResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@RestController
@RequestMapping("/admin/v1/promotion-restaurants")
@RequiredArgsConstructor
public class PromotionRestaurantAdminController {

  private final PromotionRestaurantService promotionRestaurantService;

  @PostMapping
  @AuthCheck
  public ResponseEntity<Void> createPromotionRestaurant(
      @Valid @RequestBody CreatePromotionRestaurantRequest request
  ) {
    CreatePromotionRestaurantResponse restaurantResponse =
        CreatePromotionRestaurantResponse.from(promotionRestaurantService
            .createPromotionRestaurant(request.toApplication()));

    return ResponseEntity.created(UriComponentsBuilder.fromUriString("/admin/v1/promotion-restaurants")
            .buildAndExpand(restaurantResponse.promotionRestaurantUuid())
            .toUri())
        .build();
  }

}
