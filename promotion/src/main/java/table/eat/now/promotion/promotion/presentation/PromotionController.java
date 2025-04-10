package table.eat.now.promotion.promotion.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.dto.PaginatedResultResponse;
import table.eat.now.promotion.promotion.presentation.dto.request.SearchPromotionRequest;
import table.eat.now.promotion.promotion.presentation.dto.response.GetPromotionResponse;
import table.eat.now.promotion.promotion.presentation.dto.response.SearchPromotionResponse;

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

  @GetMapping
  @AuthCheck
  public ResponseEntity<PaginatedResultResponse<SearchPromotionResponse>> searchPromotion(
      @Valid @ModelAttribute SearchPromotionRequest request
  ) {
    return ResponseEntity.ok(
        PaginatedResultResponse.from(
            promotionService.searchPromotion
                (request.toApplication())));
  }
}
