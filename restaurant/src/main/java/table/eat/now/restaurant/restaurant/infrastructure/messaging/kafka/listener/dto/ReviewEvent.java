package table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.listener.dto;

public interface ReviewEvent extends CommonEvent{
  String restaurantUuid();
}
