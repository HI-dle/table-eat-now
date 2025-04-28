/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.listener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RestaurantRatingUpdatedPayload(
    String restaurantUuid,
    BigDecimal averageRating,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime processedAt
) {

}
