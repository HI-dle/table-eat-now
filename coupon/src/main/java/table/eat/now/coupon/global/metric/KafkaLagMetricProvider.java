package table.eat.now.coupon.global.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult.ListOffsetsResultInfo;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaLagMetricProvider {

  private final AdminClient adminClient;

  public Map<TopicPartition, Long> getConsumerLag(String groupId)
      throws ExecutionException, InterruptedException {

    Map<TopicPartition, OffsetAndMetadata> consumerOffsets =
        adminClient.listConsumerGroupOffsets(groupId)
            .partitionsToOffsetAndMetadata()
            .get();

    Map<TopicPartition, ListOffsetsResultInfo> endOffsets = adminClient.listOffsets(
            consumerOffsets.keySet()
                .stream()
                .collect(
                    Collectors
                        .toMap(tp -> tp, tp -> OffsetSpec.latest())
                )
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
