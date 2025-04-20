package table.eat.now.review.application.event;

public interface ReviewEventPublisher {

  void publish(RestaurantRatingUpdateEvent ratingUpdateEvent);
}
