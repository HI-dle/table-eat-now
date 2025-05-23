package table.eat.now.review.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewReference;

public interface JpaReviewRepository extends
    JpaRepository<Review, Long>, JpaReviewRepositoryCustom {

  Optional<Review> findByReviewIdAndDeletedAtIsNull(String reviewId);

  boolean existsByReferenceAndDeletedAtIsNull(ReviewReference reference);
}
