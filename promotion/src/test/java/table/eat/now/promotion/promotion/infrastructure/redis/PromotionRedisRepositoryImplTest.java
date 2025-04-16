package table.eat.now.promotion.promotion.infrastructure.redis;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import table.eat.now.promotion.helper.IntegrationTestSupport;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
@SpringBootTest
@Slf4j
class PromotionRedisRepositoryImplTest extends IntegrationTestSupport {

  @Autowired
  private PromotionRedisRepository promotionRedisRepository;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;


  private final int maxCount = 50;

  @DisplayName("동시에_여러_유저가_참여하면_maxCount를_넘지_않는다")
  @Test
  void redis_rua_script_atomic_test() throws InterruptedException {
    //given
    int threadCount = 100;
    String promotionUuid = UUID.randomUUID().toString();
    ExecutorService executorService = Executors.newFixedThreadPool(100);
    CountDownLatch latch = new CountDownLatch(threadCount);

    //when
    for (int i = 0; i < threadCount; i++) {
      final long userId = i + 1;
      executorService.submit(() -> {
        try {
          PromotionParticipant participant = new PromotionParticipant(
              userId, promotionUuid , "테스트용 프로모션");
          boolean check = promotionRedisRepository.addUserToPromotion(participant, maxCount);
          if (check) {
            log.info("저장 : {}, ", userId);
          } else {
            log.info("count 수 초과 반환 : {}", userId);
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    //then
    String key = "promotion:테스트용 프로모션";
    Long finalCount = getRedisZSetCount(key);
    log.info(finalCount.toString());
    String lastValue = getLastRedisZSetValue(key);
    log.info("Last value: " + lastValue);

    assertThat(finalCount).isEqualTo(maxCount);
  }

  private Long getRedisZSetCount(String key) {
    return redisTemplate.opsForZSet().size(key);
  }
  private String getLastRedisZSetValue(String key) {
    Long size = stringRedisTemplate.opsForZSet().size(key);
    if (size == null || size == 0) {
      return null;
    }

    Set<TypedTuple<String>> range = stringRedisTemplate.opsForZSet()
        .rangeWithScores(key, size - 1, size - 1);

    // 첫 번째 요소를 가져와 값만 반환
    return range.stream().findFirst().map(ZSetOperations.TypedTuple::getValue).orElse(null);
  }


}