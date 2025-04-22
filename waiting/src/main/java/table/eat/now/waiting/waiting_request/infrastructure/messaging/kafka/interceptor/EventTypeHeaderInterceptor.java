package table.eat.now.waiting.waiting_request.infrastructure.messaging.kafka.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEvent;

public class EventTypeHeaderInterceptor implements ProducerInterceptor<String, WaitingRequestEvent> {

  private static final String EVENT_TYPE_HEADER = "eventType";

  @Override
  public ProducerRecord<String, WaitingRequestEvent> onSend(ProducerRecord<String, WaitingRequestEvent> record) {
    WaitingRequestEvent event = record.value();
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