package table.eat.now.review.presentation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;

@Builder
public record CreateReviewResponse(
    String reviewUuid, Long customerId, String restaurantId, String serviceId, String serviceType,
    Integer rating, String content, boolean isVisible, LocalDateTime createdAt) {

  public static CreateReviewResponse from(CreateReviewInfo info) {
    return CreateReviewResponse.builder()
        .reviewUuid(info.reviewUuid())
        .customerId(info.customerId())
        .restaurantId(info.restaurantId())
        .serviceId(info.serviceId())
        .serviceType(info.serviceType())
        .rating(info.rating())
        .content(info.content())
        .isVisible(info.isVisible())
        .createdAt(info.createdAt())
        .build();
  }
}