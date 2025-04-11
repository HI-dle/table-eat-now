package table.eat.now.review.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.request.SearchReviewQuery;
import table.eat.now.review.application.service.dto.request.UpdateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;
import table.eat.now.review.application.service.dto.response.PaginatedInfo;
import table.eat.now.review.application.service.dto.response.SearchReviewInfo;

public interface ReviewService {

  CreateReviewInfo createReview(CreateReviewCommand command);

  GetReviewInfo getReview(String reviewId, CurrentUserInfoDto userInfo);

  GetReviewInfo hideReview(String reviewId, CurrentUserInfoDto userInfo);

  GetReviewInfo showReview(String reviewId, CurrentUserInfoDto userInfo);

  GetReviewInfo updateReview(String reviewId, UpdateReviewCommand command);

  PaginatedInfo<SearchReviewInfo> getReviews(SearchReviewQuery query, CurrentUserInfoDto userInfo);

  void deleteReview(String reviewId, CurrentUserInfoDto userInfo);
}
