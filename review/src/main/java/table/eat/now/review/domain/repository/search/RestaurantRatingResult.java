package table.eat.now.review.domain.repository.search;

import java.math.BigDecimal;

public record RestaurantRatingResult(
    String restaurantId,
    BigDecimal averageRating
) {

}

