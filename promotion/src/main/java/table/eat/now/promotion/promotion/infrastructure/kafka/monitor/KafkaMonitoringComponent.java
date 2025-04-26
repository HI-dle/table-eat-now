package table.eat.now.promotion.promotion.infrastructure.kafka.monitor;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 26.
 */
@Component
public class KafkaMonitoringComponent {

  private final AdminClient adminClient;
  private final String bootstrapServers;

  public KafkaMonitoringComponent(
      AdminClient adminClient,
      @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
    this.adminClient = adminClient;
    this.bootstrapServers = bootstrapServers;
  }

  public Map<TopicPartition, Long> getConsumerLag(String groupId)
      throws InterruptedException, ExecutionException {
    Map<TopicPartition, Long> consumerLag = new HashMap<>();

    Map<TopicPartition, OffsetAndMetadata> consumerOffsets =
        adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();

    try (KafkaConsumer<String, String> consumer = createConsumer()) {
      Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumerOffsets.keySet());

      for (TopicPartition partition : consumerOffsets.keySet()) {
        long consumerOffset = consumerOffsets.get(partition).offset();
        long endOffset = endOffsets.get(partition);
        long lag = Math.max(0, endOffset - consumerOffset);
        consumerLag.put(partition, lag);
      }

      return consumerLag;
    }
  }

  private KafkaConsumer<String, String> createConsumer() {
    Properties props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());
    props.put("group.id", "kafka-monitoring-group");
    props.put("auto.offset.reset", "latest");
    return new KafkaConsumer<>(props);
  }
}

