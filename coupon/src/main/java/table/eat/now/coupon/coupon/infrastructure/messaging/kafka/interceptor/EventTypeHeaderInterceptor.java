package table.eat.now.coupon.coupon.infrastructure.messaging.kafka.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import table.eat.now.coupon.coupon.application.messaging.event.CommonEvent;

public class EventTypeHeaderInterceptor implements ProducerInterceptor<String, CommonEvent> {

  private static final String EVENT_TYPE_HEADER = "eventType";

  @Override
  public ProducerRecord<String, CommonEvent> onSend(ProducerRecord<String, CommonEvent> record) {
    CommonEvent event = record.value();
    if (event == null || event.eventType() == null) {
      return record;
    }
    record
        .headers()
        .add(EVENT_TYPE_HEADER, event.eventType().getBytes(StandardCharsets.UTF_8));
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