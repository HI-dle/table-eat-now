package table.eat.now.coupon.coupon.infrastructure.messaging.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.messaging.EventPublisher;
import table.eat.now.coupon.coupon.application.messaging.event.CouponEvent;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.coupon.infrastructure.messaging.kafka.config.CouponTopicConfig;

@RequiredArgsConstructor
@Component
public class CouponKafkaEventPublisher implements EventPublisher<CouponRequestedIssueEvent> {

  private final KafkaTemplate<String, CouponEvent> kafkaCouponTemplate;

  @Override
  public void publish(CouponRequestedIssueEvent event) {
    kafkaCouponTemplate.send(CouponTopicConfig.TOPIC_NAME, event.userCouponUuid(), event);
  }
}
