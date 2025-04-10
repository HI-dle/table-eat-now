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

	@Column(name = "review_uuid", nullable = false, unique = true)
	private String reviewId;

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
		if (reference == null || content == null || visibility == null) {
			throw new IllegalArgumentException("null일 수 없습니다.");
		}
	}

	public boolean isAccessible(Long userId, String role) {
		return this.visibility.isVisible() || isWriter(userId) || role.equals("MASTER");
	}

	public boolean isWriter(Long userId) {
		return this.reference.getCustomerId().equals(userId);
	}

	public Review hide(Long userId, String userRole) {
		validateCustomer(userId, userRole);
		this.visibility = this.visibility.hide(userId, userRole);
		return this;
	}

	public Review show(Long userId, String userRole) {
		validateCustomer(userId, userRole);
		this.visibility = this.visibility.show(userRole);
		return this;
	}

	private void validateCustomer(Long userId, String userRole) {
		if (userRole.equals("CUSTOMER") && !isWriter(userId)) {
			throw new IllegalArgumentException("이 작업에 대한 권한은 작성자에게만 있습니다.");
		}
	}

	public Review update(UpdateContent updateContent) {
		validateCustomer(updateContent.userId(), updateContent.userRole());
		this.content = updateContent.content();
		return this;
	}

	private Review(ReviewReference reference, ReviewContent content, ReviewVisibility visibility) {
		this.reviewId = UUID.randomUUID().toString();
		this.reference = reference;
		this.content = content;
		this.visibility = visibility;
	}

	protected Review() {
	}


}
