package table.eat.now.promotion.promotionUser.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.promotion.promotionUser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionUser.presentation.dto.request.CreatePromotionUserRequest;
import table.eat.now.promotion.promotionUser.presentation.dto.response.CreatePromotionUserResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@RestController
@RequestMapping("/api/v1/promotion-users")
@RequiredArgsConstructor
public class PromotionUserController {

  private final PromotionUserService promotionUserService;

  @PostMapping
  @AuthCheck
  public ResponseEntity<Void> createPromotionUser(
      @Valid @RequestBody CreatePromotionUserRequest request
  ) {
    CreatePromotionUserResponse userResponse = CreatePromotionUserResponse
        .from(promotionUserService
            .createPromotionUser(request.toApplication()));

    return ResponseEntity.created(UriComponentsBuilder.fromUriString("/api/v1/promotion-users")
            .buildAndExpand(userResponse.promotionUserUuid())
            .toUri())
        .build();
  }

}
