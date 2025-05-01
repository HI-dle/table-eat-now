package table.eat.now.waiting.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.WaitingClient;
import table.eat.now.waiting.waiting_request.application.messaging.EventPublisher;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEvent;

@ExtendWith(RedisTestContainerExtension.class)
@Import(DatabaseCleanUp.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @MockitoBean
  protected RestaurantClient restaurantClient;

  @MockitoBean
  protected WaitingClient waitingClient;

  @MockitoBean
  protected EventPublisher<WaitingRequestEvent> eventPublisher;

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
