package table.eat.now.promotion.promotion.infrastructure.kafka.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 26.
 */
@Configuration
public class KafkaMonitoringConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public AdminClient kafkaAdminClient() {
    Map<String, Object> config = new HashMap<>();
    config.put("bootstrap.servers", bootstrapServers);
    return AdminClient.create(config);
  }

}
