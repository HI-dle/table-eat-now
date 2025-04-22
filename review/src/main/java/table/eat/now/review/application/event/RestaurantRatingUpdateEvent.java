package table.eat.now.review.application.event;

import static table.eat.now.review.application.event.EventType.RATING_UPDATED;

public record RestaurantRatingUpdateEvent(
    EventType eventType,
    String restaurantUuid,
    RestaurantRatingUpdatePayload payload
) implements ReviewEvent {

  public static RestaurantRatingUpdateEvent of(RestaurantRatingUpdatePayload payload) {
    return new RestaurantRatingUpdateEvent(
        RATING_UPDATED,
        payload.restaurantUuid(),
        payload
    );
  }
}

