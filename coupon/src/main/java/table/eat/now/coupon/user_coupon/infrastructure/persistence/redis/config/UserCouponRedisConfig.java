package table.eat.now.coupon.user_coupon.infrastructure.persistence.redis.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserCouponRedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

//  @Value("${spring.data.redis.password}")
//  private String password;

  private static final String REDISSON_HOST_PREFIX = "redis://";

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
        //.setPassword(password);
    return Redisson.create(config);
  }
}
