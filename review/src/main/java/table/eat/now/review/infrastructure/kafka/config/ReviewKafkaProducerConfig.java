package table.eat.now.review.infrastructure.kafka.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import table.eat.now.review.application.event.ReviewEvent;
import table.eat.now.review.infrastructure.kafka.interceptor.EventTypeHeaderInterceptor;

@Configuration
public class ReviewKafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  private Map<String, Object> getCommonProducerProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, EventTypeHeaderInterceptor.class.getName());

    return props;
  }

  @Bean
  public ProducerFactory<String, ReviewEvent> producerFactory() {
    return new DefaultKafkaProducerFactory<>(getCommonProducerProps());
  }

  @Bean
  public KafkaTemplate<String, ReviewEvent> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ProducerFactory<String, ReviewEvent> batchProducerFactory() {
    Map<String, Object> props = getCommonProducerProps();

    props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384 * 4); // 64KB
    props.put(ProducerConfig.LINGER_MS_CONFIG, 100); // 100ms
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    props.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 3회
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // 재시도 간격 1초
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 요청 타임아웃 30초
    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 180000); // 전체 타임아웃 3분

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, ReviewEvent> batchKafkaTemplate() {
    return new KafkaTemplate<>(batchProducerFactory());
  }
}
