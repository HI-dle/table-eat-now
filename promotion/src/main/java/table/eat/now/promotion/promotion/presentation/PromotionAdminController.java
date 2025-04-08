package table.eat.now.promotion.promotion.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.dto.request.CreatePromotionRequest;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/promotions")
public class PromotionAdminController {

  private final PromotionService promotionService;

  @PostMapping
  @AuthCheck(roles = UserRole.MASTER)
  public ResponseEntity<Void> createPromotion(
      @Valid @RequestBody CreatePromotionRequest request) {
    CreatePromotionInfo promotion = promotionService.createPromotion(request.toApplication());

    return ResponseEntity.created(UriComponentsBuilder.fromUriString("/admin/v1/promotions")
        .buildAndExpand(promotion.promotionUuid())
        .toUri())
        .build();
  }

}
