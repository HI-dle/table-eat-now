package table.eat.now.coupon.global.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.global.metric.KafkaLagMetricProvider;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.config.UserCouponConsumerConfig;


@RequiredArgsConstructor
@Component
public class KafkaLagMeasureScheduler {
  private final KafkaLagMetricProvider kafkaLagMetricProvider;
  private static final List<String> GROUP_IDS = List.of(
      UserCouponConsumerConfig.GROUP,
      UserCouponConsumerConfig.GROUP_1,
      "for-test");

  @Scheduled(fixedRate = 5000)
  public void updateConsumerLag() {
    GROUP_IDS.forEach(kafkaLagMetricProvider::publishConsumerLagToMetrics);
  }
}
