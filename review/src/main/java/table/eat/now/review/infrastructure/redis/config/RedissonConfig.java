package table.eat.now.review.infrastructure.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

  private static final String REDISSON_HOST_PREFIX = "redis://";

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.username:}")
  private String redisUsername;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  @Bean(destroyMethod = "shutdown")
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
            .setUsername(redisUsername)
            .setPassword(redisPassword)
            .setConnectTimeout(5000)
            .setIdleConnectionTimeout(10000);
    return Redisson.create(config);
  }
}
