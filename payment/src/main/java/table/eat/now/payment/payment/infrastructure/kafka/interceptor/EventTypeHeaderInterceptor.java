package table.eat.now.payment.payment.infrastructure.kafka.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import table.eat.now.payment.payment.application.event.PaymentEvent;

public class EventTypeHeaderInterceptor implements ProducerInterceptor<String, PaymentEvent> {

  private static final String EVENT_TYPE_HEADER = "eventType";

  @Override
  public ProducerRecord<String, PaymentEvent> onSend(ProducerRecord<String, PaymentEvent> record) {
    PaymentEvent event = record.value();
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