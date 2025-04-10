package table.eat.now.review.application.service.dto.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record SearchReviewQuery(String orderBy,
																String sort,
																LocalDate startDate,
																LocalDate endDate,
																Integer minRating,
																Integer maxRating,
																String serviceType,
																String restaurantId, Long userId,
																int page, int size) {

}
