package table.eat.now.review.presentation;

import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.common.resolver.annotation.CurrentUserInfo;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.ReviewService;
import table.eat.now.review.presentation.dto.request.CreateReviewRequest;
import table.eat.now.review.presentation.dto.response.CreateReviewResponse;
import table.eat.now.review.presentation.dto.response.GetReviewResponse;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<CreateReviewResponse> createReview (
			@RequestBody @Valid CreateReviewRequest request,
			@CurrentUserInfo CurrentUserInfoDto userInfo){

		return ResponseEntity.status(HttpStatus.CREATED).body(
				CreateReviewResponse.from(reviewService.createReview(request.toCommand(userInfo))));
	}

	@GetMapping("/{reviewId}")
	public ResponseEntity<GetReviewResponse> getReview(
			@PathVariable String reviewId, @CurrentUserInfo CurrentUserInfoDto userInfo){

		return ResponseEntity.status(HttpStatus.OK).body(
				GetReviewResponse.from(reviewService.getReview(reviewId, userInfo)));
	}
}
