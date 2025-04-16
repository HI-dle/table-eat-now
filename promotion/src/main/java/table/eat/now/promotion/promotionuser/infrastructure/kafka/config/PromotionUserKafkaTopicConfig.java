package table.eat.now.promotion.promotionuser.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PromotionUserKafkaTopicConfig {

  private static final String PROMOTION_TOPIC_NAME = "promotion-event";

  @Bean
  public NewTopic createTopic() {
    return TopicBuilder.name(PROMOTION_TOPIC_NAME)
        .partitions(3)
        .replicas(3)
        .config("min.insync.replicas", "2")
        .build();
  }

  @Bean
  public String paymentTopic() {
    return PROMOTION_TOPIC_NAME;
  }
}