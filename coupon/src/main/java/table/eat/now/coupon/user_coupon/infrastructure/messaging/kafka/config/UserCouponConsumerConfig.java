package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.EventType;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.PromotionEvent;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.PromotionParticipatedCouponEvent;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.ReservationCancelledEvent;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto.ReservationEvent;

@Configuration
public class UserCouponConsumerConfig {

  public static final String TABLE_EAT_NOW = "table.eat.now.**";

  public final static String GROUP = "user-coupon-group-0";
  public final static String GROUP_1 = "user-coupon-group-1";
  public final static String RESERVATION_EVENT = "reservation-event";
  public final static String RESERVATION_EVENT_DLT = "reservation-event-dlt";
  public static final String PROMOTION_EVENT = "promotion-event";
  public static final String PROMOTION_EVENT_DLT = "promotion-event-dlt";

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;
  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String autoOffsetReset;
  @Value("${spring.kafka.consumer.enable-auto-commit}")
  private boolean enableAutoCommit;

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

  private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(
      ConsumerFactory<String, T> consumerFactory, String eventTypeName) {

    ConcurrentKafkaListenerContainerFactory<String, T> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setRecordFilterStrategy(createEventTypeFilter(eventTypeName));
    factory.setAckDiscarded(true);
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

  public ConsumerFactory<String, ReservationCancelledEvent> reservationCancelledEventConsumerFactory() {
    return createConsumerFactory(
        GROUP, ReservationCancelledEvent.class, this::getCommonConsumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent>
  reservationCancelledEventKafkaListenerContainerFactory(KafkaTemplate<String, ReservationEvent> kafkaTemplate) {
    ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent> factory =
        createContainerFactory(
            reservationCancelledEventConsumerFactory(), EventType.RESERVATION_CANCELLED.toString());

    DefaultErrorHandler errorHandler = getDefaultErrorHandler(kafkaTemplate, RESERVATION_EVENT_DLT);

    factory.setCommonErrorHandler(errorHandler);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent>
  reservationCancelledEventDltKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent> factory =
        createContainerFactory(
            reservationCancelledEventConsumerFactory(), EventType.RESERVATION_CANCELLED.toString());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL);
    return factory;
  }



  private Map<String, Object> getBatchConsumerProps(String groupId) {
    Map<String, Object> props = getCommonConsumerProps(groupId);
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5분
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 5000); // 5초
    return props;
  }

  public ConsumerFactory<String, PromotionParticipatedCouponEvent>
  promotionParticipatedCouponEventConsumerFactory() {
    return createConsumerFactory(
        GROUP_1, PromotionParticipatedCouponEvent.class, this::getBatchConsumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionParticipatedCouponEvent>
  promotionParticipatedCouponEventKafkaListenerContainerFactory(KafkaTemplate<String, PromotionEvent> kafkaPromotionTemplate) {
    ConcurrentKafkaListenerContainerFactory<String, PromotionParticipatedCouponEvent> factory =
        createContainerFactory(
            promotionParticipatedCouponEventConsumerFactory(), EventType.PROMOTION_PARTICIPATED_COUPON.toString());

    DefaultErrorHandler errorHandler = getDefaultErrorHandler(kafkaPromotionTemplate, PROMOTION_EVENT_DLT);

    factory.setCommonErrorHandler(errorHandler);
    factory.getContainerProperties().setIdleBetweenPolls(5000);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PromotionParticipatedCouponEvent>
  promotionParticipatedCouponEventDltKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, PromotionParticipatedCouponEvent> factory =
        createContainerFactory(
            promotionParticipatedCouponEventConsumerFactory(), EventType.PROMOTION_PARTICIPATED_COUPON.toString());

    factory.getContainerProperties().setIdleBetweenPolls(60000);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }
}
