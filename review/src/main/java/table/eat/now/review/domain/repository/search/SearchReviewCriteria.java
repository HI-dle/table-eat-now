package table.eat.now.review.domain.repository.search;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.review.domain.entity.ServiceType;

@Builder
public record SearchReviewCriteria(
    String orderBy,
    String sort,
    LocalDate startDate,
    LocalDate endDate,
    Integer minRating,
    Integer maxRating,
    ServiceType serviceType,
    String restaurantId,
    Long userId,
    Boolean isVisible,
    Long currentUserId,
    int page,
    int size) {

}
