package table.eat.now.payment.payment.infrastructure.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class PaymentKafkaErrorHandlerConfig {

  private static final String DLT_SUFFIX = "-dlt";

  @Bean
  public <T> DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
      KafkaTemplate<String, T> kafkaTemplate) {
    return new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        (ConsumerRecord<?, ?> record, Exception ex) -> {
          log.error("메시지 처리 실패: topic={}, partition={}, offset={}, exception={}",
              record.topic(), record.partition(), record.offset(), ex.getMessage());
          return new TopicPartition(record.topic()+DLT_SUFFIX, record.partition());
        }
    );
  }

  @Bean
  public DefaultErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {
    return new DefaultErrorHandler(recoverer, new FixedBackOff(200L, 5));
  }

  @Bean
  public DefaultErrorHandler dltKafkaErrorHandler() {
    return new DefaultErrorHandler(
        (record, exception) -> {
      log.warn("DLT에서 메시지 최종 처리 실패: topic={}, partition={}, offset={}, key={}",
          record.topic(), record.partition(), record.offset(), record.key());
      log.warn("최종 실패 예외 내용: {}", exception.getMessage());
      log.warn("실패한 메시지 내용: {}", record.value());
    },
        new FixedBackOff(1000L, 3));
  }
}


