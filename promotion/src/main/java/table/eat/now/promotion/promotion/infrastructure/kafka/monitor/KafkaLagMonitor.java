package table.eat.now.promotion.promotion.infrastructure.kafka.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.Map;
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

  private final KafkaMonitoringService kafkaMonitoringService;
  private final MeterRegistry meterRegistry;

  // 여러 그룹에 대해 측정할 경우 리스트로 확장 가능
  private static final String[] GROUP_IDS = {
      "promotion-schedule-consumer",
      "promotionUser-save-consumer"
  };

  @Scheduled(fixedDelay = 10000)
  public void monitorKafkaLag() {
    for (String groupId : GROUP_IDS) {
      try {
        Map<TopicPartition, Long> lags = kafkaMonitoringService.getConsumerLag(groupId);

        lags.forEach((tp, lag) -> meterRegistry.gauge(
            "kafka.consumer.lag",
            Tags.of("group", groupId, "topic", tp.topic(), "partition", String.valueOf(tp.partition())),
            lag
        ));
      } catch (Exception e) {
        System.err.printf("Kafka lag 측정 실패 (groupId: %s): %s%n", groupId, e.getMessage());
      }
    }
  }
}
