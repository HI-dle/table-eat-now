package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import table.eat.now.common.domain.BaseEntity;

@Getter
@Entity
@Table(name = "p_review")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "review_uuid", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
	private UUID reviewId;

	@Embedded
	private ReviewReference reference;

	@Embedded
	private ReviewContent content;

	@Embedded
	private ReviewVisibility visibility;

	public static Review create(
			ReviewReference reference, ReviewContent content, ReviewVisibility visibility) {
		validateNull(reference, content, visibility);
		return new Review(reference, content, visibility);
	}

	private static void validateNull(
			ReviewReference reference, ReviewContent content, ReviewVisibility visibility) {
		validateReference(reference);
		validateContent(content);
		validateVisibility(visibility);
	}

	private static void validateReference(ReviewReference reference) {
		if (reference == null) {
			throw new IllegalArgumentException("ReviewReference는 null일 수 없습니다.");
		}
	}

	private static void validateContent(ReviewContent content) {
		if (content == null) {
			throw new IllegalArgumentException("ReviewContent는 null일 수 없습니다.");
		}
	}

	private static void validateVisibility(ReviewVisibility visibility) {
		if (visibility == null) {
			throw new IllegalArgumentException("ReviewVisibility는 null일 수 없습니다.");
		}
	}


	private Review(ReviewReference reference, ReviewContent content, ReviewVisibility visibility) {
		this.reviewId = UUID.randomUUID();
		this.reference = reference;
		this.content = content;
		this.visibility = visibility;
	}

	protected Review() {
	}
}
