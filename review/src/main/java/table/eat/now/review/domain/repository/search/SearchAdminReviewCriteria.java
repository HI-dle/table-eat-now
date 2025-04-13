package table.eat.now.review.domain.repository.search;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.review.domain.entity.ServiceType;

@Builder
public record SearchAdminReviewCriteria(
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
    String accessibleRestaurantId,
    boolean isMaster,
    int page,
    int size
) {

}
