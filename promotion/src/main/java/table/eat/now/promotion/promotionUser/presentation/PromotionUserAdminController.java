package table.eat.now.promotion.promotionUser.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.promotion.promotionUser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionUser.presentation.dto.PaginatedResultResponse;
import table.eat.now.promotion.promotionUser.presentation.dto.request.SearchPromotionUserRequest;
import table.eat.now.promotion.promotionUser.presentation.dto.request.UpdatePromotionUserRequest;
import table.eat.now.promotion.promotionUser.presentation.dto.response.SearchPromotionUserResponse;
import table.eat.now.promotion.promotionUser.presentation.dto.response.UpdatePromotionUserResponse;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@RestController
@RequestMapping("/admin/v1/promotion-users")
@RequiredArgsConstructor
public class PromotionUserAdminController {

  private final PromotionUserService promotionUserService;

  @PutMapping("/{promotionUserUuid}")
  @AuthCheck
  public ResponseEntity<UpdatePromotionUserResponse> updatePromotionUser(
      @Valid @RequestBody UpdatePromotionUserRequest request,
      @PathVariable("promotionUserUuid") String promotionUserUuid
  ) {
    return ResponseEntity.ok(
        UpdatePromotionUserResponse.from(promotionUserService
            .updatePromotionUser(request.toApplication(), promotionUserUuid)));
  }

  @GetMapping
  @AuthCheck(roles = {UserRole.MASTER})
  public ResponseEntity<PaginatedResultResponse<SearchPromotionUserResponse>>
  searchPromotionUser(
      @Valid @ModelAttribute SearchPromotionUserRequest request
  ) {
    return ResponseEntity.ok(
        PaginatedResultResponse.from(
            promotionUserService.searchPromotionUser(
                request.toApplication())));
  }


}
