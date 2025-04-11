package table.eat.now.review.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.aop.annotation.AuthCheck;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.application.service.ReviewService;
import table.eat.now.review.presentation.dto.request.CreateReviewRequest;
import table.eat.now.review.presentation.dto.request.SearchReviewRequest;
import table.eat.now.review.presentation.dto.request.UpdateReviewRequest;
import table.eat.now.review.presentation.dto.response.CreateReviewResponse;
import table.eat.now.review.presentation.dto.response.GetReviewResponse;
import table.eat.now.review.presentation.dto.response.PaginatedResponse;
import table.eat.now.review.presentation.dto.response.SearchReviewResponse;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<CreateReviewResponse> createReview(
      @RequestBody @Valid CreateReviewRequest request,
      @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.status(HttpStatus.CREATED).body(
        CreateReviewResponse.from(reviewService.createReview(request.toCommand(userInfo))));
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<GetReviewResponse> getReview(
      @PathVariable UUID reviewId, @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.status(HttpStatus.OK).body(
        GetReviewResponse.from(reviewService.getReview(reviewId.toString(), userInfo)));
  }

  @PatchMapping("/{reviewId}/hide")
  public ResponseEntity<GetReviewResponse> hideReview(
      @PathVariable UUID reviewId, @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.status(HttpStatus.OK).body(
        GetReviewResponse.from(reviewService.hideReview(reviewId.toString(), userInfo)));
  }

  @PatchMapping("/{reviewId}/show")
  public ResponseEntity<GetReviewResponse> showReview(
      @PathVariable UUID reviewId, @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.status(HttpStatus.OK).body(
        GetReviewResponse.from(reviewService.showReview(reviewId.toString(), userInfo)));
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<GetReviewResponse> updateReview(
      @PathVariable UUID reviewId,
      @RequestBody @Valid UpdateReviewRequest request,
      @CurrentUserInfo CurrentUserInfoDto userInfo) {

    return ResponseEntity.status(HttpStatus.OK).body(
        GetReviewResponse.from(
            reviewService.updateReview(reviewId.toString(), request.toCommand(userInfo))));
  }

  @GetMapping
  public ResponseEntity<PaginatedResponse<SearchReviewResponse>> getReviews(
      @CurrentUserInfo CurrentUserInfoDto userInfo,
      @Valid SearchReviewRequest request, Pageable pageable) {

    return ResponseEntity.status(HttpStatus.OK).body(
        PaginatedResponse.from(
            reviewService.getReviews(request.toQuery(pageable), userInfo)));
  }

  @DeleteMapping("/{reviewId}")
  @AuthCheck(roles = {UserRole.MASTER, UserRole.CUSTOMER})
  public ResponseEntity<Void> deleteReview(
      @PathVariable UUID reviewId, @CurrentUserInfo CurrentUserInfoDto userInfo) {

    reviewService.deleteReview(reviewId.toString(), userInfo);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
