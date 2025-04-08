package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReviewContent {
	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	protected ReviewContent() {
	}
}
