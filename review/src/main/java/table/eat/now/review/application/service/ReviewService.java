package table.eat.now.review.application.service;

import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;

public interface ReviewService {

	CreateReviewInfo createReview(CreateReviewCommand command);
}
