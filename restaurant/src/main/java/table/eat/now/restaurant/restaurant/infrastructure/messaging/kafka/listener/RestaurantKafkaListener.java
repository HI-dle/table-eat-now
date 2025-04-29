/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.listener;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.application.service.dto.request.RestaurantRatingUpdatedCommand;
import table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.config.RestaurantKafkaConsumerConfig.ListenerContainerFactoryName;
import table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.config.RestaurantKafkaConsumerConfig.TopicName;
import table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.listener.dto.RestaurantRatingUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantKafkaListener {
  private final RestaurantService restaurantService;

  @KafkaListener(
      topics = TopicName.REVIEW_EVENT,
      containerFactory = ListenerContainerFactoryName.RESTAURANT_RATING_UPDATE_EVENT
  )
  public void listenRestaurantRatingUpdateEvent(
      List<ConsumerRecord<String, RestaurantRatingUpdatedEvent>> record, Acknowledgment ack) {
    log.info("식당 리뷰 평점 성공 이벤트 처리: {} 건", record.size());
    try {
      List<RestaurantRatingUpdatedCommand> list = record.stream()
          .map((r) -> r.value().toCommand())
          .toList();
      restaurantService.batchModifyRestaurantRating(list);
      ack.acknowledge();
    } catch (Throwable e) {
      log.warn("식당 리뷰 평점 성공 이벤트 처리 예외 발생: {} 건", record.size(), e);
      throw e;
    }
  }
}
