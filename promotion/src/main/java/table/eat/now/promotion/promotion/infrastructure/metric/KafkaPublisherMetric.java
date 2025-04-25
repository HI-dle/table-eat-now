package table.eat.now.promotion.promotion.infrastructure.metric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 26.
 */
@Component
@RequiredArgsConstructor
public class KafkaPublisherMetric {

  private final MeterRegistry meterRegistry;

  public void incrementSuccess(String topic) {
    meterRegistry.counter("kafka.publish.success", "topic", topic).increment();
  }

  public void incrementFail(String topic) {
    meterRegistry.counter("kafka.publish.fail", "topic", topic).increment();
  }

}
