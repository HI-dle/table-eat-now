package table.eat.now.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.repository.ReviewRepository;

public interface JpaReviewRepository extends JpaRepository<Review, Long>, ReviewRepository {

}
