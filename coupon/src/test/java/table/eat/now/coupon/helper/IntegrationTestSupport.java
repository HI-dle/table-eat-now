package table.eat.now.coupon.helper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
    statements = "ALTER TABLE p_user_coupon ALTER COLUMN id SET DEFAULT NEXT VALUE FOR p_user_coupon_seq")
@ExtendWith(RedisTestContainerExtension.class)
@Import(DatabaseCleanUp.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @Autowired
  protected RedisTemplate<String, Object> redisTemplate;

  @AfterEach
  void tearDown() {
    databaseCleanUp.afterPropertiesSet();
    databaseCleanUp.execute();
    clearRedis();
  }

  void clearRedis() {
    redisTemplate.delete(redisTemplate.keys("*"));
  }

  protected Exception extractExceptionFromFuture(Future<?> future) {
    try {
      future.get(3, TimeUnit.SECONDS);
    } catch (ExecutionException e) {
      return (Exception) e.getCause();
    } catch (Exception e) {
      return e;
    }
    return null;
  }
}
