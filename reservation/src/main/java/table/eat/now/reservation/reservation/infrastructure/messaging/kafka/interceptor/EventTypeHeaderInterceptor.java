/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import table.eat.now.reservation.reservation.application.event.event.ReservationEvent;

public class EventTypeHeaderInterceptor implements
    ProducerInterceptor<String, ReservationEvent> {
  private static final String EVENT_TYPE_HEADER = "eventType";

  @Override
  public ProducerRecord<String, ReservationEvent> onSend(ProducerRecord<String, ReservationEvent> record) {
    ReservationEvent event = record.value();
    if (event == null || event.eventType() == null) {
      return record;
    }
    record
        .headers()
        .add(EVENT_TYPE_HEADER, event.eventType().name().getBytes(StandardCharsets.UTF_8));
    return record;
  }

  @Override
  public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
  }

  @Override
  public void close() {
  }

  @Override
  public void configure(Map<String, ?> map) {
  }
}
