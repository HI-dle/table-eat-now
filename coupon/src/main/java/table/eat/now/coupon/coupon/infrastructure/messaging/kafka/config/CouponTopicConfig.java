package table.eat.now.coupon.coupon.infrastructure.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class CouponTopicConfig {

  public static final String TOPIC_NAME = "coupon-event";
  public static final String TOPIC_DLT_NAME = "coupon-event-dlt";

  @Value("${kafka.topic.coupon.partitions:3}")
  private int partitions;
  @Value("${kafka.topic.coupon.replicas:1}")
  private int replicas;
  @Value("${kafka.topic.coupon.min-insync-replicas:1}")
  private String minInsyncReplicas;

  @Bean
  public NewTopic createCouponTopic() {
    return TopicBuilder.name(TOPIC_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }

  @Bean
  public NewTopic createCouponDltTopic() {
    return TopicBuilder.name(TOPIC_DLT_NAME)
        .partitions(partitions)
        .replicas(replicas)
        .config("min.insync.replicas", minInsyncReplicas)
        .build();
  }
}