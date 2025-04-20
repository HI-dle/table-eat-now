package table.eat.now.notification.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class NotificationKafkaTopicConfig {

  private static final String NOTIFICATION_TOPIC_NAME = "notification-event";

  @Bean
  public NewTopic createTopic() {
    return TopicBuilder.name(NOTIFICATION_TOPIC_NAME)
        .partitions(3)
        .replicas(3)
        .config("min.insync.replicas", "2")
        .build();
  }

  @Bean
  public String notificationTopic() {
    return NOTIFICATION_TOPIC_NAME;
  }
}