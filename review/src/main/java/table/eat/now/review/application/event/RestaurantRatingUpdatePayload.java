package table.eat.now.review.application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

public record RestaurantRatingUpdatePayload(
    String restaurantUuid,
    BigDecimal averageRating,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime processedAt
) {
  public static RestaurantRatingUpdatePayload from(RestaurantRatingResult ratingUpdates) {
    return new RestaurantRatingUpdatePayload(
        ratingUpdates.restaurantUuid(),
        ratingUpdates.averageRating(),
        LocalDateTime.now()
    );
  }
}
