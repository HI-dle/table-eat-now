package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.config;

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
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.PromotionEvent;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.ReservationEvent;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.interceptor.EventTypeHeaderInterceptor;

@Configuration
public class UserCouponProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  private static final int BATCH_SIZE = 16384 * 5; // 80KB
  private static final int LINGER_MS = 50;
  private static final int BUFFER_MEMORY = 33554432; // 32MB
  private static final String COMPRESSION_TYPE = "snappy";

  private Map<String, Object> getCommonProducerProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 3);
    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, EventTypeHeaderInterceptor.class.getName());

    return props;
  }

  public <T> ProducerFactory<String, T> producerFactory() {
    return new DefaultKafkaProducerFactory<>(getCommonProducerProps());
  }

  public <T> ProducerFactory<String, T> batchProducerFactory() {
    Map<String, Object> props = getCommonProducerProps();

    props.put(ProducerConfig.BATCH_SIZE_CONFIG, BATCH_SIZE);
    props.put(ProducerConfig.LINGER_MS_CONFIG, LINGER_MS);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY);
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, COMPRESSION_TYPE);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, ReservationEvent> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public KafkaTemplate<String, PromotionEvent> kafkaPromotionTemplate() {
    return new KafkaTemplate<>(batchProducerFactory());
  }
}
