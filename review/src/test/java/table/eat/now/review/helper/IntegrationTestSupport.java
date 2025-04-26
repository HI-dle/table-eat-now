package table.eat.now.review.helper;

import org.junit.jupiter.api.AfterAll;
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
  protected RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

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
