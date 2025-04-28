/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.listener.dto;

import table.eat.now.restaurant.restaurant.application.service.dto.request.RestaurantRatingUpdatedCommand;

public record RestaurantRatingUpdatedEvent(
    String eventType, // RATING_UPDATED
    String restaurantUuid,
    RestaurantRatingUpdatedPayload payload
) implements ReviewEvent {

  public RestaurantRatingUpdatedCommand toCommand() {
    return RestaurantRatingUpdatedCommand.builder()
        .restaurantUuid(payload.restaurantUuid())
        .averageRating(payload.averageRating())
        .build();
  }
}
