package table.eat.now.review.application.service.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.review.domain.repository.search.SearchReviewResult;

@Builder
public record SearchReviewInfo(
		String reviewUuid,
		Long customerId,
		String restaurantId,
		String serviceId,
		String serviceType,
		Integer rating,
		String content,
		boolean isVisible,
		Long hiddenBy,
		String hiddenByRole,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {

	public static SearchReviewInfo from(SearchReviewResult result) {
		return SearchReviewInfo.builder()
				.reviewUuid(result.reviewUuid())
				.customerId(result.customerId())
				.restaurantId(result.restaurantId())
				.serviceId(result.serviceId())
				.serviceType(result.serviceType())
				.rating(result.rating())
				.content(result.content())
				.isVisible(result.isVisible())
				.hiddenBy(result.hiddenBy())
				.hiddenByRole(result.hiddenByRole())
				.createdAt(result.createdAt())
				.updatedAt(result.updatedAt())
				.build();
	}

}
