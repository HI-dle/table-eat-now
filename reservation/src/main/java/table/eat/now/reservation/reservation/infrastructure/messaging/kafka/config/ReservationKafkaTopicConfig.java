/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ReservationKafkaTopicConfig {

  @Value("${kafka.topic.reservation.partitions:3}")
  private int partitions;
  @Value("${kafka.topic.reservation.replicas:1}")
  private int replicas;
  @Value("${kafka.topic.reservation.min-insync-replicas:1}")
  private String reservationMinInsyncReplicas;

  @Value("${kafka.topic.reservation-dlt.partitions:3}")
  private int reservationDltPartitions;
  @Value("${kafka.topic.reservation-dlt.replicas:1}")
  private int reservationDltReplicas;
  @Value("${kafka.topic.reservation-dlt.min-insync-replicas:1}")
  private String reservationDltMinInsyncReplicas;

  @Bean
  public NewTopic createReservationTopic() {
    return TopicBuilder.name(TopicName.RESERVATION_EVENT)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", reservationMinInsyncReplicas)
        .build();
  }

  @Bean
  public NewTopic reservationDltTopic() {
    return TopicBuilder.name(TopicName.RESERVATION_EVENT_DLT)
        .partitions(reservationDltPartitions)
        .replicas(reservationDltReplicas)
        .config("min.insync.replicas", reservationDltMinInsyncReplicas)
        .build();
  }

  public static class TopicName {
    public static final String RESERVATION_EVENT = "reservation-event";
    public static final String RESERVATION_EVENT_DLT = "reservation-event-dlt";
  }
}
