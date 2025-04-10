package table.eat.now.review.domain.repository.search;

import java.time.LocalDate;

public record SearchReviewCriteria(String orderBy,
																	 String sort,
																	 LocalDate startDate,
																	 LocalDate endDate,
																	 Integer minRating,
																	 Integer maxRating,
																	 String serviceType,
																	 String restaurantId, Long userId,
																	 int page, int size) {

}
