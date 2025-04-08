package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;
import lombok.Getter;

@Getter
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

	public static ReviewReference create(
			UUID restaurantId, UUID serviceId, Long customerId, ServiceType serviceType) {
		validateNull(restaurantId, serviceId, customerId, serviceType);
		return new ReviewReference(restaurantId, serviceId, customerId, serviceType);
	}

	private static void validateNull(
			UUID restaurantId, UUID serviceId, Long customerId, ServiceType serviceType) {
		if (restaurantId == null || serviceId == null || customerId == null || serviceType == null) {
			throw new IllegalArgumentException("null이 될 수 없습니다.");
		}
	}

	private ReviewReference(UUID restaurantId, UUID serviceId, Long customerId,
			ServiceType serviceType) {
		this.restaurantId = restaurantId;
		this.serviceId = serviceId;
		this.customerId = customerId;
		this.serviceType = serviceType;
	}

	protected ReviewReference() {
	}
}
