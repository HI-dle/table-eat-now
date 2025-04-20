package table.eat.now.review.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ReviewKafkaTopicConfig {

  private static final String REVIEW_TOPIC_NAME = "review-event";
  @Value("${kafka-config.topic.partitions}")
  private int partitions;
  @Value("${kafka-config.topic.replicas}")
  private int replicas;
  @Value("${kafka-config.topic.min-insync-replicas}")
  private String minInsyncReplicas;

  @Bean
  public NewTopic createTopic() {
    return TopicBuilder.name(REVIEW_TOPIC_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }

  @Bean
  public String reviewTopic() {
    return REVIEW_TOPIC_NAME;
  }
}