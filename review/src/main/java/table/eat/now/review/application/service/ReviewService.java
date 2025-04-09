package table.eat.now.review.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;

public interface ReviewService {

	CreateReviewInfo createReview(CreateReviewCommand command);

	GetReviewInfo getReview(String reviewId, CurrentUserInfoDto userInfo);
}
