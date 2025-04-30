package table.eat.now.coupon.coupon.infrastructure.persistence.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CouponRedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

//  @Value("${spring.data.redis.password}")
//  private String password;

  private static final String REDISSON_HOST_PREFIX = "redis://";

  @Bean
  public RedisTemplate<String, Object> couponRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    ObjectMapper objectMapperForRedis = new ObjectMapper();
    objectMapperForRedis.registerModule(new JavaTimeModule());
    //objectMapperForRedis.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //objectMapperForRedis.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<>(objectMapperForRedis, Object.class);

    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(serializer);

    return redisTemplate;
  }

  @Bean
  public RedissonClient couponRedissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
    //.setPassword(password);
    return Redisson.create(config);
  }
}