package table.eat.now.review.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import table.eat.now.review.application.service.dto.request.SearchAdminReviewQuery;
import table.eat.now.review.application.service.dto.request.SearchReviewQuery;

public record SearchAdminReviewRequest(
    @Pattern(regexp = "^(WAITING|RESERVATION)$") String serviceType,
    @Pattern(regexp = "^(createdAt|rating)$") String orderBy,
    @Pattern(regexp = "^(desc|asc)$") String sort,
    @PastOrPresent LocalDate startDate,
    @PastOrPresent LocalDate endDate,
    @Max(4) Integer minRating,
    @Min(1) Integer maxRating,
    UUID restaurantId,
    Long userId,
    Boolean isVisible
) {

  public SearchAdminReviewRequest {
    orderBy = orderBy != null ? orderBy : "createdAt";
    sort = sort != null ? sort : "desc";
  }

  public SearchAdminReviewQuery toQuery(Pageable pageable) {
    return SearchAdminReviewQuery.builder()
        .orderBy(orderBy)
        .sort(sort)
        .startDate(startDate)
        .endDate(endDate)
        .minRating(minRating)
        .maxRating(maxRating)
        .serviceType(serviceType)
        .restaurantId(restaurantId != null ? restaurantId.toString() : null)
        .userId(userId)
        .isVisible(isVisible)
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .build();
  }

}
