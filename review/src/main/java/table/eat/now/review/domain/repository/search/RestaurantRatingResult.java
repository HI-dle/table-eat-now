package table.eat.now.review.domain.repository.search;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record RestaurantRatingResult(
    String restaurantUuid,
    BigDecimal averageRating
) {

  public RestaurantRatingResult {
    averageRating = averageRating.setScale(2, RoundingMode.HALF_UP);
  }
}

