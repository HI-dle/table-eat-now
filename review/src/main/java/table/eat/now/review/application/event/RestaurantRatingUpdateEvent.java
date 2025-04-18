package table.eat.now.review.application.event;

import static table.eat.now.review.application.event.EventType.RATING_UPDATE;

import java.util.UUID;

public record RestaurantRatingUpdateEvent(
    EventType eventType,
    String eventId,
    RestaurantRatingUpdatePayload payload
) implements ReviewEvent {

  public static RestaurantRatingUpdateEvent of(RestaurantRatingUpdatePayload payload) {
    return new RestaurantRatingUpdateEvent(
        RATING_UPDATE,
        UUID.randomUUID().toString(),
        payload);
  }
}

