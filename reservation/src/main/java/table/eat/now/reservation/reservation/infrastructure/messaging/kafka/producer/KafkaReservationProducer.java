/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.producer;

import static table.eat.now.reservation.reservation.infrastructure.messaging.kafka.config.ReservationKafkaTopicConfig.TopicName.RESERVATION_EVENT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.event.event.ReservationEvent;
import table.eat.now.reservation.reservation.application.event.publisher.ReservationEventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReservationProducer implements ReservationEventPublisher <ReservationEvent> {

  private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

  @Override
  public void publish(ReservationEvent event) {
    kafkaTemplate.send(RESERVATION_EVENT, event.reservationUuid(), event);
  }

}
