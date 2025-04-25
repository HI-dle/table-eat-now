package table.eat.now.promotion.promotionuser.infrastructure.kafka.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import table.eat.now.promotion.promotionuser.application.event.EventType;
import table.eat.now.promotion.promotionuser.infrastructure.kafka.dto.PromotionUserSaveEvent;

@EnableKafka
@Configuration
public class PromotionUserKafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;
  private static final String PROMOTION_TOPIC_DLT = "promotion-event-dlt";

  // 공통 기본 설정 생성 메서드
  private Map<String, Object> getCommonConsumerProps(String groupId) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    return props;
  }

  // 타입별 컨슈머 팩토리 생성 메서드
  private <T> ConsumerFactory<String, T> createConsumerFactory(
      Class<T> targetType, String groupId) {
    Map<String, Object> props = getCommonConsumerProps(groupId);

    JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetType);
    jsonDeserializer.setUseTypeHeaders(false);
    jsonDeserializer.setRemoveTypeHeaders(true);

    return new DefaultKafkaConsumerFactory<>(
        props,
        new StringDeserializer(),
        jsonDeserializer
    );
  }

  // 이벤트 타입 헤더 필터 생성 메서드
  private <T> RecordFilterStrategy<String, T> createEventTypeFilter(String eventTypeName) {
    return record -> {
      Header eventTypeHeader = record.headers().lastHeader("eventType");
      if (eventTypeHeader == null) {
        return true; // 헤더가 없으면 필터링
      }
      String eventTypeValue = new String(eventTypeHeader.value(), StandardCharsets.UTF_8);
      return !eventTypeValue.equals(eventTypeName); // 지정된 이벤트 타입만 통과
    };
  }

  // 타입별 컨테이너 팩토리 생성 메서드
  private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(
      ConsumerFactory<String, T> consumerFactory, String eventTypeName) {

    ConcurrentKafkaListenerContainerFactory<String, T> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setRecordFilterStrategy(createEventTypeFilter(eventTypeName));

    return factory;
  }

  private <T> DefaultErrorHandler getDefaultErrorHandler(
      KafkaTemplate<String, T> kafkaTemplate, String topicName) {

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        (record, ex) ->
            new TopicPartition(topicName, record.partition()));

    return new DefaultErrorHandler(
        recoverer,
        new FixedBackOff(1000L, 3));
  }

  //promotionUserSaveEvent 컨슈머 팩토리
  @Bean
  public ConsumerFactory<String, PromotionUserSaveEvent> successEventConsumerFactory() {
    return createConsumerFactory(PromotionUserSaveEvent.class, "promotionUser-save-consumer");
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionUserSaveEvent>
  createPromotionUserEventKafkaListenerContainerFactory(
      KafkaTemplate<String, PromotionUserSaveEvent> kafkaTemplate
  ) {
    ConcurrentKafkaListenerContainerFactory<String, PromotionUserSaveEvent> factory =
        createContainerFactory(successEventConsumerFactory(), EventType.SUCCEED.name());

    factory.setCommonErrorHandler(getDefaultErrorHandler(kafkaTemplate, PROMOTION_TOPIC_DLT));
    return factory;
  }

  @Bean
  public ConsumerFactory<String, PromotionUserSaveEvent> dltConsumerFactory() {
    return createConsumerFactory(PromotionUserSaveEvent.class, "promotionUser-dlt-consumer");
  }
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionUserSaveEvent>
  promotionUserEventDltKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, PromotionUserSaveEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(dltConsumerFactory());
    factory.getContainerProperties().setAckMode(
        org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL
    );
    return factory;
  }
}