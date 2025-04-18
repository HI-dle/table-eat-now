package table.eat.now.payment.payment.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class PaymentKafkaTopicConfig {

  private static final String PAYMENT_TOPIC_NAME = "payment-event";

  @Bean
  public NewTopic createTopic() {
    return TopicBuilder.name(PAYMENT_TOPIC_NAME)
        .partitions(3)
        .replicas(3)
        .config("min.insync.replicas", "2")
        .build();
  }

  @Bean
  public String paymentTopic() {
    return PAYMENT_TOPIC_NAME;
  }
}