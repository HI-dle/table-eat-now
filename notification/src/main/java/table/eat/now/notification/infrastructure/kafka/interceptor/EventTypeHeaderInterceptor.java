package table.eat.now.notification.infrastructure.kafka.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import table.eat.now.notification.application.event.NotificationEvent;


public class EventTypeHeaderInterceptor implements ProducerInterceptor<String, NotificationEvent> {

  private static final String EVENT_TYPE_HEADER = "eventType";

  @Override
  public ProducerRecord<String, NotificationEvent> onSend(ProducerRecord<String, NotificationEvent> record) {
    NotificationEvent event = record.value();
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