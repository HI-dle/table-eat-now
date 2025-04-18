package table.eat.now.review.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.repository.search.PaginatedResult;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;
import table.eat.now.review.domain.repository.search.SearchAdminReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchAdminReviewResult;
import table.eat.now.review.domain.repository.search.SearchReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchReviewResult;

public interface ReviewRepository {

  Review save(Review review);

  Optional<Review> findByReviewIdAndDeletedAtIsNull(String reviewId);

  PaginatedResult<SearchReviewResult> searchReviews(SearchReviewCriteria criteria);

  PaginatedResult<SearchAdminReviewResult> searchAdminReviews(SearchAdminReviewCriteria criteria);

  boolean existsByReferenceAndDeletedAtIsNull(ReviewReference reference);

  List<RestaurantRatingResult> calculateRestaurantRatings(List<String> restaurantIds);

  List<String> findRecentlyUpdatedRestaurantIds(LocalDateTime updatedAfter, long offset, int limit);

  long countRecentlyUpdatedRestaurants(LocalDateTime updatedAfter);
}
