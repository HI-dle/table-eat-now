package table.eat.now.coupon.global.scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.global.metric.KafkaLagMetricProvider;
import table.eat.now.coupon.global.metric.MetricProvider;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.config.UserCouponConsumerConfig;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaLagMeasureScheduler {
  private final KafkaLagMetricProvider kafkaLagMetricProvider;
  private final MetricProvider metricProvider;
  private final Map<String, AtomicLong> lagMetrics = new ConcurrentHashMap<>();
  private static final List<String> GROUP_IDS = List.of(
      UserCouponConsumerConfig.GROUP, UserCouponConsumerConfig.GROUP_1, "for-test");

  @Scheduled(fixedRate = 5000)
  public void updateConsumerLag() {
    GROUP_IDS.forEach(this::publishConsumerLagToMetrics);
  }

  private void publishConsumerLagToMetrics(String groupId) {

    Map<TopicPartition, Long> consumerLag = null;
    try {
      consumerLag = kafkaLagMetricProvider.getConsumerLag(groupId);

      consumerLag.forEach((topicPartition, lag) -> {
        String metricId = String.format("%s:%s:%d", groupId, topicPartition.topic(), topicPartition.partition());
        AtomicLong atomicLag = lagMetrics.computeIfAbsent(metricId, id -> {
          AtomicLong value = new AtomicLong(lag);
          metricProvider.getLagGauge(groupId, topicPartition, value);
          return value;
        });
        atomicLag.set(lag);
      });
    }
    catch (Exception e) {
      log.error("컨슈머 랙 지표 획득 실패:: groupId:{}", groupId, e);
    }
  }
}
