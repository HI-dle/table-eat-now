package table.eat.now.waiting.waiting_request.infrastructure.messaging.kafka.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import table.eat.now.waiting.waiting_request.application.event.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.infrastructure.messaging.kafka.interceptor.EventTypeHeaderInterceptor;

@Configuration
public class WaitingRequestProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, WaitingRequestEvent> producerFactory() {

    Map<String, Object> props = new HashMap<>();
    props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
    props.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, 3);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, EventTypeHeaderInterceptor.class.getName());

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, WaitingRequestEvent> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
