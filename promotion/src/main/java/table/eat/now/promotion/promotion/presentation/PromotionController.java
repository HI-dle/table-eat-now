package table.eat.now.promotion.promotion.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.dto.PaginatedResultResponse;
import table.eat.now.promotion.promotion.presentation.dto.request.ParticipatePromotionUserRequest;
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
      @PathVariable("promotionUuid") UUID promotionUuid
  ) {
    return ResponseEntity.ok(
        GetPromotionResponse.from(promotionService.findPromotion(promotionUuid.toString())));
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

  @PostMapping("/event/participate")
  public ResponseEntity<?> participate(
      @RequestBody ParticipatePromotionUserRequest request
  ) {
    boolean success = promotionService.participate(request.toApplication());
    if (success) {
      return ResponseEntity.ok(request.promotionName() + "에 참여 성공했습니다.");
    } else {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
          .body("정원이 마감되었습니다.");
    }
  }
}
