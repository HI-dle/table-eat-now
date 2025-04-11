package table.eat.now.promotion.promotionrestaurant.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.promotion.promotionrestaurant.application.service.PromotionRestaurantService;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.PaginatedResultResponse;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.request.CreatePromotionRestaurantRequest;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.request.SearchPromotionRestaurantRequest;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.request.UpdatePromotionRestaurantRequest;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.response.CreatePromotionRestaurantResponse;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.response.SearchPromotionRestaurantResponse;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.response.UpdatePromotionRestaurantResponse;

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
  @AuthCheck(roles = {UserRole.MASTER, UserRole.OWNER})
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
  @PutMapping("/{promotionRestaurantUuid}")
  @AuthCheck(roles = {UserRole.MASTER})
  public ResponseEntity<UpdatePromotionRestaurantResponse> updatePromotionRestaurant(
    @Valid @RequestBody UpdatePromotionRestaurantRequest request,
    @PathVariable("promotionRestaurantUuid") String promotionRestaurantUuid) {
    return ResponseEntity.ok(
        UpdatePromotionRestaurantResponse.from(promotionRestaurantService
            .updatePromotionRestaurant(request.toApplication(), promotionRestaurantUuid)));
  }

  @GetMapping
  @AuthCheck(roles = {UserRole.MASTER})
  public ResponseEntity<PaginatedResultResponse<SearchPromotionRestaurantResponse>>
  searchPromotionRestaurant(
      @Valid @ModelAttribute SearchPromotionRestaurantRequest request
  ) {
    return ResponseEntity.ok(
        PaginatedResultResponse.from(
            promotionRestaurantService.searchPromotionRestaurant(
                request.toApplication())));
  }

  @DeleteMapping("/{restaurantUuid}")
  @AuthCheck(roles = UserRole.MASTER)
  public ResponseEntity<Void> deletePromotionRestaurant(
      @PathVariable("restaurantUuid") String restaurantUuid,
      @CurrentUserInfo CurrentUserInfoDto userInfoDto
  ) {
    promotionRestaurantService.deletePromotionRestaurant(restaurantUuid, userInfoDto);
    return ResponseEntity.noContent().build();
  }
}
