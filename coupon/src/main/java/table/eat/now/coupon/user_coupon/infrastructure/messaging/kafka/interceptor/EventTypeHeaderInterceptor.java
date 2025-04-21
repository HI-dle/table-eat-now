package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.KafkaCommonEvent;

public class EventTypeHeaderInterceptor implements ProducerInterceptor<String, KafkaCommonEvent> {

  private static final String EVENT_TYPE_HEADER = "eventType";

  @Override
  public ProducerRecord<String, KafkaCommonEvent> onSend(ProducerRecord<String, KafkaCommonEvent> record) {
    KafkaCommonEvent event = record.value();
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