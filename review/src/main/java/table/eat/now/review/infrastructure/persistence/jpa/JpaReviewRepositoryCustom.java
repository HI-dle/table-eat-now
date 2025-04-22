package table.eat.now.review.infrastructure.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import table.eat.now.review.domain.repository.search.PaginatedResult;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;
import table.eat.now.review.domain.repository.search.SearchAdminReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchAdminReviewResult;
import table.eat.now.review.domain.repository.search.SearchReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchReviewResult;

public interface JpaReviewRepositoryCustom {

  PaginatedResult<SearchReviewResult> searchReviews(SearchReviewCriteria criteria);

  PaginatedResult<SearchAdminReviewResult> searchAdminReviews(SearchAdminReviewCriteria criteria);

  List<RestaurantRatingResult> calculateRestaurantRatings(List<String> restaurantIds);

  List<String> findRecentlyUpdatedRestaurantIds(
      LocalDateTime startTime, LocalDateTime endTime, long offset, int limit);
}
