package table.eat.now.review.application.service.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.review.domain.entity.Review;

@Builder
public record GetReviewInfo(
		String reviewUuid, Long customerId, String restaurantId,
		String serviceId, String serviceType,
		Integer rating, String content, boolean isVisible,
		LocalDateTime createdAt, LocalDateTime updatedAt
) {
	public static GetReviewInfo from(Review review){
		return GetReviewInfo.builder()
				.reviewUuid(review.getReviewId())
				.customerId(review.getReference().getCustomerId())
				.restaurantId(review.getReference().getRestaurantId())
				.serviceId(review.getReference().getServiceId())
				.serviceType(review.getReference().getServiceType().name())
				.rating(review.getContent().getRating())
				.content(review.getContent().getContent())
				.isVisible(review.getVisibility().isVisible())
				.createdAt(review.getCreatedAt())
				.updatedAt(review.getUpdatedAt())
				.build();
	}
}
