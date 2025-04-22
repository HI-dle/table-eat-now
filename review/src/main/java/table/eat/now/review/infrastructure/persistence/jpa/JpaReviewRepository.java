package table.eat.now.review.infrastructure.persistence.jpa;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.repository.ReviewRepository;

public interface JpaReviewRepository extends
    JpaRepository<Review, Long>, ReviewRepository, JpaReviewRepositoryCustom {

  Optional<Review> findByReviewIdAndDeletedAtIsNull(String reviewId);

  boolean existsByReferenceAndDeletedAtIsNull(ReviewReference reference);

  @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT r.restaurant_uuid
            FROM p_review r
            WHERE r.updated_at BETWEEN :startTime AND :endTime
            GROUP BY r.restaurant_uuid
        ) AS grouped
      """, nativeQuery = true)
  long countRecentlyUpdatedRestaurants(@Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);
}
