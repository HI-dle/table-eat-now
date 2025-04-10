package table.eat.now.promotion.promotion.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.dto.response.GetPromotionResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionService promotionService;

  @GetMapping("/{promotionUuid}")
  @AuthCheck
  public ResponseEntity<GetPromotionResponse> findPromotion(
      @PathVariable("promotionUuid") String promotionUuid
  ) {
    return ResponseEntity.ok(
        GetPromotionResponse.from(promotionService.findPromotion(promotionUuid)));
  }
}
