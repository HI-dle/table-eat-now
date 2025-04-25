package table.eat.now.promotion.promotion.infrastructure.kafka.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.produce.PromotionScheduleEvent;

@EnableKafka
@Configuration
public class PromotionKafkaConsumerConfig {

  private static final String TABLE_EAT_NOW = "table.eat.now.**";
  private static final String PROMOTION_SCHEDULE_GROUP = "promotion-schedule-consumer";
  private static final String PROMOTION_TOPIC_DLT = "promotion-event-dlt";

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;
  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String autoOffsetReset;
  @Value("${spring.kafka.consumer.enable-auto-commit}")
  private boolean enableAutoCommit;
  @Value("${spring.kafka.consumer.fetch-min-bytes}")
  private int fetchMinBytes;

  @Value("${spring.kafka.consumer.fetch-max-wait-ms}")
  private int fetchMaxWaitMs;

  @Value("${spring.kafka.consumer.max-poll-records}")
  private int maxPollRecords;

  private static <T> JsonDeserializer<T> getTJsonDeserializer(Class<T> targetType) {
    JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetType);
    jsonDeserializer.setUseTypeHeaders(false);
    jsonDeserializer.setRemoveTypeHeaders(true);
    jsonDeserializer.addTrustedPackages(TABLE_EAT_NOW);
    return jsonDeserializer;
  }

  private Map<String, Object> getCommonConsumerProps(String groupId) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
    props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
    return props;
  }

  private <T> ConsumerFactory<String, T> createConsumerFactory(
      String groupId, Class<T> targetType, Function<String, Map<String, Object>> function) {

    return new DefaultKafkaConsumerFactory<>(
        function.apply(groupId),
        new StringDeserializer(),
        getTJsonDeserializer(targetType)
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
    factory.setConcurrency(3);

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

  //promotionSchedule 컨슈머 팩토리
  @Bean
  public ConsumerFactory<String, PromotionScheduleEvent> scheduleEventConsumerFactory() {
    return createConsumerFactory(
        PROMOTION_SCHEDULE_GROUP,PromotionScheduleEvent.class, this::getCommonConsumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionScheduleEvent>
  promotionScheduleEventKafkaListenerContainerFactory(KafkaTemplate<String, PromotionScheduleEvent> kafkaTemplate) {
    ConcurrentKafkaListenerContainerFactory<String, PromotionScheduleEvent> factory =
        createContainerFactory(
            scheduleEventConsumerFactory(),
            Arrays.asList(EventType.START.name(), EventType.END.name())); //두 개의 이벤트를 처리

    factory.setCommonErrorHandler(
        getDefaultErrorHandler(kafkaTemplate, PROMOTION_TOPIC_DLT));

    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionScheduleEvent>
  promotionScheduleEventDltKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, PromotionScheduleEvent> factory =
        createContainerFactory(
            scheduleEventConsumerFactory(),
            Arrays.asList(EventType.START.name(), EventType.END.name()));
    factory.getContainerProperties().setAckMode(AckMode.MANUAL);
    return factory;
  }


}