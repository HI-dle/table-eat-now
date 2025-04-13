package table.eat.now.review.application.service.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.search.SearchAdminReviewCriteria;

@Builder
public record SearchAdminReviewQuery(
    String orderBy,
    String sort,
    LocalDate startDate,
    LocalDate endDate,
    Integer minRating,
    Integer maxRating,
    String serviceType,
    String restaurantId,
    Long userId,
    Boolean isVisible,
    int page, int size) {

  public SearchAdminReviewCriteria toCriteria(String accessibleRestaurantId, boolean isMaster) {
    return SearchAdminReviewCriteria.builder()
        .orderBy(orderBy)
        .sort(sort)
        .startDate(startDate)
        .endDate(endDate)
        .minRating(minRating)
        .maxRating(maxRating)
        .serviceType(serviceType == null ? null : ServiceType.from(serviceType))
        .restaurantId(restaurantId)
        .userId(userId)
        .isVisible(isVisible)
        .accessibleRestaurantId(accessibleRestaurantId)
        .isMaster(isMaster)
        .page(page)
        .size(size)
        .build();
  }

}
