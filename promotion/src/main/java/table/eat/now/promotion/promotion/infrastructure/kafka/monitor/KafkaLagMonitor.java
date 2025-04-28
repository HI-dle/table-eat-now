package table.eat.now.promotion.promotion.infrastructure.kafka.monitor;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 26.
 */
@Component
@RequiredArgsConstructor
public class KafkaLagMonitor {

  private final KafkaMonitoringComponent kafkaMonitoringComponent;
  private final MeterRegistry meterRegistry;

  private static final String[] GROUP_IDS = {
      "promotion-schedule-consumer",
      "promotionUser-save-consumer"
  };

  private final Map<String, AtomicLong> lagMetrics = new ConcurrentHashMap<>();

  @Scheduled(fixedDelay = 10000)
  public void monitorKafkaLag() {
    for (String groupId : GROUP_IDS) {
      try {
        Map<TopicPartition, Long> lags = kafkaMonitoringComponent.getConsumerLag(groupId);

        lags.forEach((tp, lagValue) -> {
          String key = groupId + "|" + tp.topic() + "|" + tp.partition();
          AtomicLong lagMetric = lagMetrics.computeIfAbsent(key, k -> {
            AtomicLong atomicLong = new AtomicLong(0L);
            Gauge.builder("kafka_consumer_lag", atomicLong, AtomicLong::get)
                .tags("group", groupId, "topic", tp.topic(), "partition", String.valueOf(tp.partition()))
                .register(meterRegistry);
            return atomicLong;
          });
          lagMetric.set(lagValue);
        });

      } catch (Exception e) {
        System.err.printf("Kafka lag 측정 실패 (groupId: %s): %s%n", groupId, e.getMessage());
      }
    }
  }
}

