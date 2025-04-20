package table.eat.now.reservation.reservation.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ReservationKafkaTopicConfig {
  public static final String TOPIC_NAME = "reservation-event";
  @Value("${kafka.topic.reservation.partitions:3}")
  private int partitions;
  @Value("${kafka.topic.reservation.replicas:3}")
  private int replicas;
  @Value("${kafka.topic.reservation.min-insync-replicas:2}")
  private String minInsyncReplicas;

  @Bean
  public NewTopic createReservationTopic() {
    return TopicBuilder.name(TOPIC_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }
}
