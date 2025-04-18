package table.eat.now.review.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.event.RestaurantRatingUpdateEvent;
import table.eat.now.review.application.event.ReviewEvent;
import table.eat.now.review.application.event.ReviewEventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReviewProducer implements ReviewEventPublisher {

  private final KafkaTemplate<String, ReviewEvent> kafkaTemplate;
  private final String reviewTopic;

  @Override
  public void publish(RestaurantRatingUpdateEvent ratingUpdateEvent) {
    kafkaTemplate.send(reviewTopic, ratingUpdateEvent.eventId() ,ratingUpdateEvent);
    logEvent(ratingUpdateEvent);
  }

  private void logEvent(ReviewEvent reviewEvent) {
    log.info("Published review event {}", reviewEvent.eventType().name());
  }
}