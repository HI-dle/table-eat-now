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
}
