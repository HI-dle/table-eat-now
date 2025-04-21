package table.eat.now.review.application.event;

import static table.eat.now.review.application.event.EventType.RATING_UPDATED;

import java.util.UUID;

public record RestaurantRatingUpdateEvent(
    EventType eventType,
    String restaurantUuid,
    RestaurantRatingUpdatePayload payload
) implements ReviewEvent {

  public static RestaurantRatingUpdateEvent of(RestaurantRatingUpdatePayload payload) {
    return new RestaurantRatingUpdateEvent(
        RATING_UPDATED,
        UUID.randomUUID().toString(),
        payload
    );
  }
}

