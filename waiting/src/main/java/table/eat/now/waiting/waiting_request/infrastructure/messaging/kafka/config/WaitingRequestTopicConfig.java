package table.eat.now.waiting.waiting_request.infrastructure.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class WaitingRequestTopicConfig {

  public static final String TOPIC_NAME = "waiting-request-event";

  @Value("${kafka.topic.waiting-request.partitions:3}")
  private int partitions;
  @Value("${kafka.topic.waiting-request.replicas:3}")
  private int replicas;
  @Value("${kafka.topic.waiting-request.min-insync-replicas:2}")
  private String minInsyncReplicas;

  @Bean
  public NewTopic createWaitingRequestTopic() {
    return TopicBuilder.name(TOPIC_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }
}