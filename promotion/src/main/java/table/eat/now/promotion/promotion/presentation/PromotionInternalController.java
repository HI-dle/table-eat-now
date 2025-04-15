package table.eat.now.promotion.promotion.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.dto.request.GetPromotionsFeignRequest;
import table.eat.now.promotion.promotion.presentation.dto.response.GetPromotionsFeignResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
@RestController
@RequestMapping("/internal/v1/promotions")
@RequiredArgsConstructor
public class PromotionInternalController {

  private final PromotionService promotionService;

  @PostMapping
  public ResponseEntity<GetPromotionsFeignResponse> reservationGetPromotions(
      @RequestBody GetPromotionsFeignRequest request) {
    return ResponseEntity.ok(GetPromotionsFeignResponse.from(
        promotionService.reservationGetPromotions(request.toApplication())));
  }
}
