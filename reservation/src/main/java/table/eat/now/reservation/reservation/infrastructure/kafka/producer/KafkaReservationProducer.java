/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.infrastructure.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.event.event.ReservationEvent;
import table.eat.now.reservation.reservation.application.event.publisher.ReservationEventPublisher;
import table.eat.now.reservation.reservation.infrastructure.kafka.config.ReservationKafkaTopicConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReservationProducer implements ReservationEventPublisher <ReservationEvent> {

  private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

  @Override
  public void publish(ReservationEvent event) {
    kafkaTemplate.send(ReservationKafkaTopicConfig.TOPIC_NAME, event.reservationUuid(), event);
  }

}
