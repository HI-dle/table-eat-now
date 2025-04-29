package table.eat.now.payment.payment.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PaymentKafkaTopicConfig {

  private static final String PAYMENT_TOPIC_NAME = "payment-event";
  private static final String RESERVATION_DLT_NAME = "reservation-event-dlt";

  @Bean
  public NewTopic createTopic() {
    return TopicBuilder.name(PAYMENT_TOPIC_NAME)
        .partitions(3)
        .replicas(1)
        .config("min.insync.replicas", "1")
        .build();
  }

  @Bean
  public NewTopic createReservationDeadLetterTopic() {
    return TopicBuilder.name(RESERVATION_DLT_NAME)
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public String paymentTopic() {
    return PAYMENT_TOPIC_NAME;
  }
}