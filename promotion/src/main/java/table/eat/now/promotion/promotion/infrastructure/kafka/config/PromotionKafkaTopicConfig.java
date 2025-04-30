package table.eat.now.promotion.promotion.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PromotionKafkaTopicConfig {

  private static final String PROMOTION_TOPIC_NAME = "promotion-event";

  private static final String PROMOTION_TOPIC_DLT = "promotion-event-dlt";

  private static final String NOTIFICATION_TOPIC_NAME = "notification-event";
  private static final String NOTIFICATION_TOPIC_DLT = "notification-event-dlt";

  @Value("${kafka.topic.promotion.partitions:3}")
  private int partitions;
  @Value("${kafka.topic.promotion.replicas:3}")
  private int replicas;
  @Value("${kafka.topic.promotion.min-insync-replicas:2}")
  private String minInsyncReplicas;

  @Bean
  public NewTopic createPromotionTopic() {
    return TopicBuilder.name(PROMOTION_TOPIC_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }

  @Bean
  public String promotionTopic() {
    return PROMOTION_TOPIC_NAME;
  }

  @Bean
  public NewTopic promotionDlqTopic() {
    return TopicBuilder.name(PROMOTION_TOPIC_DLT)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }

  @Bean
  public String promotionTopicDlt() {
    return PROMOTION_TOPIC_DLT;
  }


  @Bean
  public NewTopic createNotificationTopic() {
    return TopicBuilder.name(NOTIFICATION_TOPIC_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }

  @Bean
  public String notificationTopic() {
    return NOTIFICATION_TOPIC_NAME;
  }

  @Bean
  public NewTopic notificationDlqTopic() {
    return TopicBuilder.name(NOTIFICATION_TOPIC_DLT)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }

  @Bean
  public String notificationTopicDlt() {
    return NOTIFICATION_TOPIC_DLT;
  }
}