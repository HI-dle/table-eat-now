package table.eat.now.promotion.promotion.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PromotionKafkaTopicConfig {

  private static final String PROMOTION_TOPIC_NAME = "promotion-event";
  private static final String COUPON_TOPIC_NAME = "coupon-event";

  @Bean
  public NewTopic createPromotionTopic() {
    return TopicBuilder.name(PROMOTION_TOPIC_NAME)
        .partitions(3)
        .replicas(3)
        .config("min.insync.replicas", "2")
        .build();
  }

  @Bean
  public String promotionTopic() {
    return PROMOTION_TOPIC_NAME;
  }

  @Bean
  public NewTopic createCouponTopic() {
    return TopicBuilder.name(COUPON_TOPIC_NAME)
        .partitions(3)
        .replicas(3)
        .config("min.insync.replicas", "2")
        .build();
  }

  @Bean
  public String couponTopic() {
    return COUPON_TOPIC_NAME;
  }
}