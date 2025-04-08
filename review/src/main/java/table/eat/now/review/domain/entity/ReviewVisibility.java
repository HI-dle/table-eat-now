package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class ReviewVisibility {
	@Column(name = "is_visible", nullable = false)
	private Boolean isVisible;

	@Column(name = "hidden_by")
	private Long hiddenBy;

	@Column(name = "hidden_at")
	private LocalDateTime hiddenAt;

	@Column(name = "hidden_by_role")
	private String hiddenByRole;

	protected ReviewVisibility() {
	}
}