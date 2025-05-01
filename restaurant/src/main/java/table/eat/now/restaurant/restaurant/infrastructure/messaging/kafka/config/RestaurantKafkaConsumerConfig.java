/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import table.eat.now.restaurant.restaurant.infrastructure.messaging.kafka.listener.dto.RestaurantRatingUpdatedEvent;

@Configuration
public class RestaurantKafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;
  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String autoOffsetReset;
  @Value("${spring.kafka.consumer.enable-auto-commit}")
  private boolean enableAutoCommit;
  @Value("${spring.kafka.consumer.concurrency:3}")
  private Integer concurrency;

  // 공통 기본 설정 생성 메서드
  private Map<String, Object> createCommonConsumerProps(String groupId) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
    return props;
  }

  // 타입별 컨슈머 팩토리 생성 메서드
  private <T> ConsumerFactory<String, T> createConsumerFactory(
      Map<String, Object> props, Class<T> targetType) {

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

  /**
   * MAX_POLL_RECORDS_CONFIG: 한 번의 poll에 의해 반환될 수 있는 최대 레코드 수 지정
   * <p>
   * MAX_POLL_INTERVAL_MS_CONFIG: Consumer가 Broker에게 정상적으로 응답하기 위한 최대 시간 지정
   * <p>
   * FETCH_MAX_WAIT_MS_CONFIG: Broker가 요청에 응답하기 전에 대기하는 최대 시간을 지정
   * <p>
   * 주의: FETCH_MAX_WAIT_MS_CONFIG 값은 MAX_POLL_INTERVAL_MS_CONFIG 값보다 작아야 된다.
   *
   * @return 컨슈머 펙토리
   */
  @Bean
  public ConsumerFactory<String, RestaurantRatingUpdatedEvent>
  batchRestaurantRatingUpdatedEventConsumerFactory() {
    Map<String, Object> props = createCommonConsumerProps(GroupIdName.RESTAURANT);
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600_000); // 10분
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 60_000); // 1분
    return createConsumerFactory(props, RestaurantRatingUpdatedEvent.class);
  }

  /**
   * setIdleBetweenPolls: 두 poll 사이의 최대 대기 시간을 설정
   * <p>
   * setBatchListener(true): Listener가 배치 모드로 동작
   *
   * @return 리스너 컨테이너 펙토리
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, RestaurantRatingUpdatedEvent>
  batchRestaurantRatingUpdatedEventKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, RestaurantRatingUpdatedEvent> factory =
        createContainerFactory(
            batchRestaurantRatingUpdatedEventConsumerFactory(),
            EventType.RATING_UPDATED.toString()
        );

    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    factory.getContainerProperties().setIdleBetweenPolls(60_000); // 1분
    factory.setBatchListener(true);
    factory.setConcurrency(concurrency);
    return factory;
  }

  public enum EventType {
    RATING_UPDATED,
    ;
  }

  public static class GroupIdName {

    public static final String RESTAURANT = "restaurant-group";
  }

  public static class TopicName {

    public static final String REVIEW_EVENT = "review-event";

  }

  public static class ListenerContainerFactoryName {

    public static final String RESTAURANT_RATING_UPDATE_EVENT = "batchRestaurantRatingUpdatedEventKafkaListenerContainerFactory";
  }

}
