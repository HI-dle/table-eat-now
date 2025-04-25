package table.eat.now.notification.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class NotificationKafkaTopicConfig {

  private static final String NOTIFICATION_TOPIC_NAME = "notification-event";

  @Value("${kafka.topic.promotion.partitions}")
  private int partitions;
  @Value("${kafka.topic.promotion.replicas}")
  private int replicas;
  @Value("${kafka.topic.promotion.min-insync-replicas}")
  private String minInsyncReplicas;

  @Bean
  public NewTopic createTopic() {
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
}