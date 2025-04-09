package table.eat.now.review.application.service.dto.request;

import java.util.UUID;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewContent;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.entity.ReviewVisibility;
import table.eat.now.review.domain.entity.ServiceType;

public record CreateReviewCommand(
		UUID restaurantId, UUID serviceId, Long customerId, String serviceType,
		String content, Integer rating,
		Boolean isVisible, UserRole role) {

	public Review toEntity() {
		return Review.create(
				ReviewReference.create(restaurantId, serviceId, customerId, ServiceType.from(serviceType)),
				ReviewContent.create(content, rating),
				ReviewVisibility.create(isVisible, customerId, role.name()));
	}
}
