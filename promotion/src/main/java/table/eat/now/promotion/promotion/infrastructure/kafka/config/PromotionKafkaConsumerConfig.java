package table.eat.now.promotion.promotion.infrastructure.kafka.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.produce.PromotionScheduleEvent;

@EnableKafka
@Configuration
public class PromotionKafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

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
  // 이벤트 타입 헤더 필터 생성 메서드 (여러 이벤트 타입 처리)
  private <T> RecordFilterStrategy<String, T> createEventTypeFilter(List<String> eventTypeNames) {
    return record -> {
      Header eventTypeHeader = record.headers().lastHeader("eventType");
      if (eventTypeHeader == null) {
        return true; // 헤더가 없으면 필터링
      }
      String eventTypeValue = new String(eventTypeHeader.value(), StandardCharsets.UTF_8);
      // 지정된 이벤트 타입만 통과
      return !eventTypeNames.contains(eventTypeValue);
    };
  }

  // 타입별 컨테이너 팩토리 생성 메서드 (여러 이벤트 타입 처리)
  private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(
      ConsumerFactory<String, T> consumerFactory, List<String> eventTypeNames) {

    ConcurrentKafkaListenerContainerFactory<String, T> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setRecordFilterStrategy(createEventTypeFilter(eventTypeNames));

    return factory;
  }

  //promotionSchedule 컨슈머 팩토리
  @Bean
  public ConsumerFactory<String, PromotionScheduleEvent> scheduleEventConsumerFactory() {
    return createConsumerFactory(PromotionScheduleEvent.class, "promotion-schedule-consumer");
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionScheduleEvent>
  promotionScheduleEventKafkaListenerContainerFactory() {
    return createContainerFactory(scheduleEventConsumerFactory(),
        Arrays.asList(EventType.START.name(), EventType.END.name()));  // 두 이벤트 타입을 모두 처리
  }
}