package table.eat.now.payment.payment.infrastructure.kafka.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
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
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import table.eat.now.payment.payment.infrastructure.kafka.event.EventType;
import table.eat.now.payment.payment.infrastructure.kafka.event.ReservationCancelledEvent;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class PaymentKafkaConsumerConfig {

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
  @Value("${spring.kafka.consumer.concurrency}")
  private int defaultConcurrency;
  @Value("${spring.kafka.consumer-dlt.concurrency}")
  private int dltConcurrency;

  private static final String TABLE_EAT_NOW = "table.eat.now";
  private static final String RESERVATION_CANCELLED_GROUP = "reservation-cancelled-consumer";
  private static final String RESERVATION_CANCELLED_DLT_GROUP = "reservation-cancelled-dlt-consumer";

  private final DefaultErrorHandler kafkaErrorHandler;
  private final DefaultErrorHandler dltKafkaErrorHandler;

  private <T> ConsumerFactory<String, T> createConsumerFactory(
      String groupId, Class<T> targetType, Function<String, Map<String, Object>> propProvider) {

    return new DefaultKafkaConsumerFactory<>(
        propProvider.apply(groupId),
        new StringDeserializer(),
        getJsonDeserializer(targetType)
    );
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

  private static <T> JsonDeserializer<T> getJsonDeserializer(Class<T> targetType) {
    JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetType);
    jsonDeserializer.setUseTypeHeaders(false);
    jsonDeserializer.setRemoveTypeHeaders(true);
    jsonDeserializer.addTrustedPackages(TABLE_EAT_NOW);
    return jsonDeserializer;
  }

  private <T> RecordFilterStrategy<String, T> createEventTypeFilter(String eventTypeName) {
    return record -> {
      Header eventTypeHeader = record.headers().lastHeader("eventType");
      if (eventTypeHeader == null) {
        return true;
      }
      String eventTypeValue = new String(eventTypeHeader.value(), StandardCharsets.UTF_8);
      return !eventTypeValue.equals(eventTypeName);
    };
  }

  private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(
      ConsumerFactory<String, T> consumerFactory, String eventTypeName) {

    ConcurrentKafkaListenerContainerFactory<String, T> factory =
        new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setRecordFilterStrategy(createEventTypeFilter(eventTypeName));
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    factory.setAckDiscarded(true);
    factory.setConcurrency(defaultConcurrency);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, ReservationCancelledEvent>
  reservationCancelledEventConsumerFactory() {

    return createConsumerFactory(
        RESERVATION_CANCELLED_GROUP,
        ReservationCancelledEvent.class,
        this::getCommonConsumerProps
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent>
  reservationCancelledEventListenerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent> factory =
        createContainerFactory(
            reservationCancelledEventConsumerFactory(),
            EventType.RESERVATION_CANCELLED.name()
        );

    factory.setCommonErrorHandler(kafkaErrorHandler);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, ReservationCancelledEvent>
  reservationCancelledEventDltConsumerFactory() {
    return createConsumerFactory(
        RESERVATION_CANCELLED_DLT_GROUP,
        ReservationCancelledEvent.class,
        this::getCommonConsumerProps
    );
  }


  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent>
  reservationCancelledEventDltListenerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, ReservationCancelledEvent> factory =
        createContainerFactory(
            reservationCancelledEventDltConsumerFactory(),
            EventType.RESERVATION_CANCELLED.name()
        );

    factory.setCommonErrorHandler(dltKafkaErrorHandler);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL);
    factory.setConcurrency(dltConcurrency);
    return factory;
  }
}