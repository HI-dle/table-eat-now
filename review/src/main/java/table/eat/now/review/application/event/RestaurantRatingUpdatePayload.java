package table.eat.now.review.application.event;

import java.time.LocalDateTime;
import java.util.List;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

public record RestaurantRatingUpdatePayload(
    List<RestaurantRatingResult> ratingUpdates,
    LocalDateTime processedAt
) {
  public static RestaurantRatingUpdatePayload from(List<RestaurantRatingResult> ratingUpdates) {
    return new RestaurantRatingUpdatePayload(
        ratingUpdates,
        LocalDateTime.now()
    );
  }

  public int getUpdateCount() {
    return ratingUpdates != null ? ratingUpdates.size() : 0;
  }
}
