package table.eat.now.review.domain.repository.search;

import java.time.LocalDateTime;

public record SearchReviewResult(
		String reviewUuid,
		Long customerId,
		String restaurantId,
		String serviceId,
		String serviceType,
		Integer rating,
		String content,
		Boolean isVisible,
		Long hiddenBy,
		String hiddenByRole,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {
}
