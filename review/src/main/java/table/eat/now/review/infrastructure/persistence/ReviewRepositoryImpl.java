package table.eat.now.review.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.repository.ReviewRepository;
import table.eat.now.review.domain.repository.search.CursorResult;
import table.eat.now.review.domain.repository.search.PaginatedResult;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;
import table.eat.now.review.domain.repository.search.SearchAdminReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchAdminReviewResult;
import table.eat.now.review.domain.repository.search.SearchReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchReviewResult;
import table.eat.now.review.infrastructure.persistence.jpa.JpaReviewRepository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

  private final JpaReviewRepository jpaRepository;

  @Override
  public Review save(Review review) {
    return jpaRepository.save(review);
  }

  @Override
  public Optional<Review> findByReviewIdAndDeletedAtIsNull(String reviewId) {
    return jpaRepository.findByReviewIdAndDeletedAtIsNull(reviewId);
  }

  @Override
  public PaginatedResult<SearchReviewResult> searchReviews(SearchReviewCriteria criteria) {
    return jpaRepository.searchReviews(criteria);
  }

  @Override
  public PaginatedResult<SearchAdminReviewResult> searchAdminReviews(
      SearchAdminReviewCriteria criteria) {
    return jpaRepository.searchAdminReviews(criteria);
  }

  @Override
  public boolean existsByReferenceAndDeletedAtIsNull(ReviewReference reference) {
    return jpaRepository.existsByReferenceAndDeletedAtIsNull(reference);
  }

  @Override
  public List<RestaurantRatingResult> calculateRestaurantRatings(List<String> restaurantIds) {
    return jpaRepository.calculateRestaurantRatings(restaurantIds);
  }

  @Override
  public <S extends Review> List<S> saveAllAndFlush(Iterable<S> entities) {
    return jpaRepository.saveAllAndFlush(entities);
  }

  @Override
  public List<CursorResult> findRecentlyUpdatedRestaurantIds(LocalDateTime startTime,
      LocalDateTime endTime, LocalDateTime lastUpdatedAt, String lastRestaurantId, int limit) {
    return jpaRepository.findRecentlyUpdatedRestaurantIds(
        startTime, endTime, lastUpdatedAt, lastRestaurantId, limit);
  }

  @Override
  public CursorResult findEndCursorResult(LocalDateTime endTime) {
    return jpaRepository.findEndCursorResult(endTime);
  }
}
