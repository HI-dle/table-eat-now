package table.eat.now.waiting.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(RedisTestContainerExtension.class)
@Import(DatabaseCleanUp.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @AfterEach
  void tearDown() {
    databaseCleanUp.afterPropertiesSet();
    databaseCleanUp.execute();
    clearRedis();

  }

  void clearRedis() {
    redisTemplate.delete(redisTemplate.keys("*"));
  }
}
