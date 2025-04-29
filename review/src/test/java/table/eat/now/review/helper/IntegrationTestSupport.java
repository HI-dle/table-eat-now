package table.eat.now.review.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.client.RestaurantClient;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.event.ReviewEventPublisher;

@TestPropertySource(properties = {
    "review.rating.update.batch-size=3"
})
@ExtendWith(RedisTestContainerExtension.class)
@Import(DatabaseCleanUp.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @Autowired
  protected RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private DatabaseCleanUp databaseCleanUp;

  @MockitoBean
  protected ReviewEventPublisher reviewEventPublisher;

  @MockitoBean
  protected WaitingClient waitingClient;

  @MockitoBean
  protected ReservationClient reservationClient;

  @MockitoBean
  protected RestaurantClient restaurantClient;

  @AfterEach
  void tearDown() {
    databaseCleanUp.execute();
    clearRedis();
  }

  void clearRedis() {
    redisTemplate.delete(redisTemplate.keys("*"));
  }
}
