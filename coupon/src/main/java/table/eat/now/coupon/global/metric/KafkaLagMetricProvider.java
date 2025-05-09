package table.eat.now.coupon.global.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult.ListOffsetsResultInfo;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaLagMetricProvider {

  private final AdminClient adminClient;
  private final MetricProvider metricProvider;
  private final Map<String, AtomicLong> lagMetrics = new ConcurrentHashMap<>();

  public void publishConsumerLagToMetrics(String groupId) {

    Map<TopicPartition, Long> consumerLag = null;
    try {
      consumerLag = this.getConsumerLag(groupId);

      consumerLag.forEach((topicPartition, lag) -> {
        String metricId = String.format("%s:%s:%d", groupId, topicPartition.topic(), topicPartition.partition());
        AtomicLong atomicLag = lagMetrics.computeIfAbsent(metricId, id -> {
          AtomicLong value = new AtomicLong(lag);
          metricProvider.getLagGauge(groupId, topicPartition, value);
          return value;
        });
        atomicLag.set(lag);
      });
    }
    catch (Exception e) {
      log.error("컨슈머 랙 지표 획득 실패:: groupId:{}", groupId, e);
    }
  }

  private Map<TopicPartition, Long> getConsumerLag(String groupId)
      throws ExecutionException, InterruptedException {

    Map<TopicPartition, OffsetAndMetadata> consumerOffsets =
        adminClient.listConsumerGroupOffsets(groupId)
            .partitionsToOffsetAndMetadata()
            .get();

    Map<TopicPartition, ListOffsetsResultInfo> endOffsets = adminClient.listOffsets(
            consumerOffsets.keySet()
                .stream()
                .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.latest()))
        )
        .all().get();

    Map<TopicPartition, Long> consumerLag = new HashMap<>();
    for (TopicPartition partition : consumerOffsets.keySet()) {
      long consumerOffset = consumerOffsets.get(partition).offset();
      long endOffset = endOffsets.get(partition).offset();
      long lag = Math.max(0, endOffset - consumerOffset);
      consumerLag.put(partition, lag);
    }

    return consumerLag;
  }
}
