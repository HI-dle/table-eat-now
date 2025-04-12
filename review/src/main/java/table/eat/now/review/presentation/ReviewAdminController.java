package table.eat.now.review.presentation;

import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.common.resolver.dto.UserRole.OWNER;
import static table.eat.now.common.resolver.dto.UserRole.STAFF;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.ReviewService;
import table.eat.now.review.presentation.dto.request.SearchAdminReviewRequest;
import table.eat.now.review.presentation.dto.response.PaginatedResponse;
import table.eat.now.review.presentation.dto.response.SearchAdminReviewResponse;

@RestController
@RequestMapping("/admin/v1/reviews")
@RequiredArgsConstructor
public class ReviewAdminController {

  private final ReviewService reviewService;

  @GetMapping
  @AuthCheck(roles = {MASTER, OWNER, STAFF})
  public ResponseEntity<PaginatedResponse<SearchAdminReviewResponse>> searchAdminReviews(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @Valid SearchAdminReviewRequest request, Pageable pageable) {

    return ResponseEntity.status(HttpStatus.OK).body(
        PaginatedResponse.fromAdminInfo(
            reviewService.searchAdminReviews(request.toQuery(pageable), userInfo)));
  }
}
