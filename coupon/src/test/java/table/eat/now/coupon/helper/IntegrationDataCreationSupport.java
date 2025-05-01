package table.eat.now.coupon.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public abstract class IntegrationDataCreationSupport {

  @Autowired
  protected RedisTemplate<String, Object> redisTemplate;

  void clearRedis() {
    redisTemplate.delete(redisTemplate.keys("*"));
  }
}
