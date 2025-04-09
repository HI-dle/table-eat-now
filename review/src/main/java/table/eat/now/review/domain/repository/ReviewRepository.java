package table.eat.now.review.domain.repository;

import java.util.Optional;
import java.util.UUID;
import table.eat.now.review.domain.entity.Review;

public interface ReviewRepository {

	Review save(Review review);

	Optional<Review> findByReviewIdAndDeletedAtIsNull(String reviewId);
}
