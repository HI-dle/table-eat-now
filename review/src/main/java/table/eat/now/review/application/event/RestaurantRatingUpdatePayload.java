package table.eat.now.review.application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

public record RestaurantRatingUpdatePayload(
    List<RestaurantRatingResult> ratingUpdates,
    int updateCount,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime processedAt
) {
  public static RestaurantRatingUpdatePayload from(List<RestaurantRatingResult> ratingUpdates) {
    return new RestaurantRatingUpdatePayload(
        ratingUpdates,
        ratingUpdates != null ? ratingUpdates.size() : 0,
        LocalDateTime.now()
    );
  }
}
