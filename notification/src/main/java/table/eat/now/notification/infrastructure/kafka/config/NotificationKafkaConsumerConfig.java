package table.eat.now.notification.infrastructure.kafka.config;

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
import table.eat.now.notification.application.event.EventType;
import table.eat.now.notification.application.event.produce.NotificationScheduleSendEvent;
import table.eat.now.notification.application.event.produce.NotificationSendEvent;

@EnableKafka
@Configuration
public class NotificationKafkaConsumerConfig {

  private static final String TABLE_EAT_NOW = "table.eat.now.**";
  private static final String NOTIFICATION_SEND_GROUP = "Notification-send-consumer";
  private static final String NOTIFICATION_EVENT_DLT = "Notification-event-dlt";

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
    JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType);
    deserializer.setUseTypeHeaders(false);
    deserializer.setRemoveTypeHeaders(true);
    deserializer.addTrustedPackages(TABLE_EAT_NOW);
    return deserializer;
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
      String groupId, Class<T> targetType, Function<String, Map<String, Object>> propProvider) {

    return new DefaultKafkaConsumerFactory<>(
        propProvider.apply(groupId),
        new StringDeserializer(),
        getTJsonDeserializer(targetType)
    );
  }

  private <T> RecordFilterStrategy<String, T> createEventTypeFilter(String eventTypeName) {
    return record -> {
      Header eventTypeHeader = record.headers().lastHeader("eventType");
      if (eventTypeHeader == null) return true;
      String eventTypeValue = new String(eventTypeHeader.value(), StandardCharsets.UTF_8);
      return !eventTypeValue.equals(eventTypeName);
    };
  }

  private <T> DefaultErrorHandler getDefaultErrorHandler(
      KafkaTemplate<String, T> kafkaTemplate, String dltTopicName) {

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        (record, ex) -> new TopicPartition(dltTopicName, record.partition())
    );

    return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
  }

  private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(
      ConsumerFactory<String, T> consumerFactory,
      String eventTypeName) {

    ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setRecordFilterStrategy(createEventTypeFilter(eventTypeName));
    factory.setAckDiscarded(true);
    factory.setConcurrency(3);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, NotificationSendEvent> sendEventConsumerFactory() {
    return createConsumerFactory(
        NOTIFICATION_SEND_GROUP, NotificationSendEvent.class, this::getCommonConsumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, NotificationSendEvent>
  sendNotificationEventKafkaListenerContainerFactory(KafkaTemplate<String, NotificationSendEvent> kafkaTemplate) {

    ConcurrentKafkaListenerContainerFactory<String, NotificationSendEvent> factory =
        createContainerFactory(sendEventConsumerFactory(), EventType.SEND.name());

    factory.setCommonErrorHandler(getDefaultErrorHandler(kafkaTemplate, NOTIFICATION_EVENT_DLT));
    factory.getContainerProperties().setIdleBetweenPolls(5000);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }


  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, NotificationSendEvent>
  notificationSendEventDltKafkaListenerContainerFactory(KafkaTemplate<String, NotificationSendEvent> kafkaTemplate) {

    ConcurrentKafkaListenerContainerFactory<String, NotificationSendEvent> factory =
        createContainerFactory(sendEventConsumerFactory(), EventType.SEND.name());

    factory.setCommonErrorHandler(getDefaultErrorHandler(kafkaTemplate, NOTIFICATION_EVENT_DLT));
    factory.getContainerProperties().setIdleBetweenPolls(60000);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, NotificationScheduleSendEvent> scheduleSendEventConsumerFactory() {
    return createConsumerFactory(
        NOTIFICATION_SEND_GROUP, NotificationScheduleSendEvent.class, this::getCommonConsumerProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, NotificationScheduleSendEvent>
  scheduleSendNotificationEventKafkaListenerContainerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, NotificationScheduleSendEvent> factory =
        createContainerFactory(scheduleSendEventConsumerFactory(), EventType.SCHEDULE_SEND.name());

    factory.getContainerProperties().setIdleBetweenPolls(5000);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, NotificationScheduleSendEvent>
  scheduleSendNotificationEventDltKafkaListenerContainerFactory(KafkaTemplate<String, NotificationScheduleSendEvent> kafkaTemplate) {

    ConcurrentKafkaListenerContainerFactory<String, NotificationScheduleSendEvent> factory =
        createContainerFactory(scheduleSendEventConsumerFactory(), EventType.SCHEDULE_SEND.name());

    factory.setCommonErrorHandler(getDefaultErrorHandler(kafkaTemplate, NOTIFICATION_EVENT_DLT));
    factory.getContainerProperties().setIdleBetweenPolls(60000);
    factory.setBatchListener(true);
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
  }
}