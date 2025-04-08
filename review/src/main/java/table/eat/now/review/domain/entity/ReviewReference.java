package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;

@Embeddable
public class ReviewReference {

	@Column(name = "restaurant_uuid", nullable = false, columnDefinition = "VARCHAR(100)")
	private UUID restaurantId;

	@Column(name = "service_uuid", nullable = false, columnDefinition = "VARCHAR(100)")
	private UUID serviceId;

	@Column(name = "customer_id", nullable = false, columnDefinition = "VARCHAR(100)")
	private Long customerId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType;

	protected ReviewReference() {
	}
}
