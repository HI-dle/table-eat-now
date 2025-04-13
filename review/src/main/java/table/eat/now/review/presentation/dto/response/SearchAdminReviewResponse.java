package table.eat.now.review.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.review.application.service.dto.response.SearchAdminReviewInfo;

@Builder
public record SearchAdminReviewResponse(
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

  public static SearchAdminReviewResponse from(SearchAdminReviewInfo info) {
    return SearchAdminReviewResponse.builder()
        .reviewUuid(info.reviewUuid())
        .customerId(info.customerId())
        .restaurantId(info.restaurantId())
        .serviceId(info.serviceId())
        .serviceType(info.serviceType())
        .rating(info.rating())
        .content(info.content())
        .isVisible(info.isVisible())
        .hiddenBy(info.hiddenBy())
        .hiddenByRole(info.hiddenByRole())
        .createdAt(info.createdAt())
        .updatedAt(info.updatedAt())
        .build();
  }

}
