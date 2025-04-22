/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 22.
 */
package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.config;

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
import table.eat.now.reservation.reservation.application.event.event.ReservationEvent;
import table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listner.dto.EventType;
import table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listner.dto.ReservationPaymentSucceedEvent;

@Configuration
public class ReservationConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;
  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String autoOffsetReset;
  @Value("${spring.kafka.consumer.enable-auto-commit}")
  private boolean enableAutoCommit;

  // 공통 기본 설정 생성 메서드
  private Map<String, Object> getCommonConsumerProps(String groupId) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
    return props;
  }

  // 타입별 컨슈머 팩토리 생성 메서드
  private <T> ConsumerFactory<String, T> createConsumerFactory(
      String groupId, Class<T> targetType) {
    Map<String, Object> props = getCommonConsumerProps(groupId);

    JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetType);
    jsonDeserializer.setUseTypeHeaders(false);
    jsonDeserializer.setRemoveTypeHeaders(true);
    jsonDeserializer.addTrustedPackages("table.eat.now.**");

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
    factory.setAckDiscarded(true);
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

  @Bean
  public ConsumerFactory<String, ReservationPaymentSucceedEvent>
  reservationPaymentSucceedEventConsumerFactory() {
    return createConsumerFactory(GroupIdName.RESERVATION, ReservationPaymentSucceedEvent.class);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReservationPaymentSucceedEvent>
  reservationPaymentSucceedEventKafkaListenerContainerFactory(
      KafkaTemplate<String, ReservationEvent> kafkaTemplate) {
    ConcurrentKafkaListenerContainerFactory<String, ReservationPaymentSucceedEvent> factory =
        createContainerFactory(
            reservationPaymentSucceedEventConsumerFactory(),
            EventType.RESERVATION_PAYMENT_SUCCEED.toString());

    DefaultErrorHandler errorHandler = getDefaultErrorHandler(kafkaTemplate,
        TopicName.PAYMENT_EVENT);

    factory.setCommonErrorHandler(errorHandler);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }


  // dlt
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReservationPaymentSucceedEvent>
  reservationPaymentSucceedEventDltKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, ReservationPaymentSucceedEvent> factory =
        createContainerFactory(
            reservationPaymentSucceedEventConsumerFactory(),
            EventType.RESERVATION_PAYMENT_SUCCEED.toString());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL);
    return factory;
  }

  public static class GroupIdName {

    public static final String RESERVATION = "reservation-group";
  }

  public static class TopicName {

    public static final String PAYMENT_EVENT = "payment-event";
    public static final String PAYMENT_EVENT_DLT = "payment-event-dlt";
  }

  public static class ListenerContainerFactoryName {

    public static final String RESERVATION_PAYMENT_SUCCEED_EVENT = "reservationPaymentSucceedEventKafkaListenerContainerFactory";
    public static final String RESERVATION_PAYMENT_SUCCEED_EVENT_DLT = "reservationPaymentSucceedEventKafkaListenerContainerFactory";
  }
}
