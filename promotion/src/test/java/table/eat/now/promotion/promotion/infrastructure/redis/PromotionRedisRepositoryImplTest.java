package table.eat.now.promotion.promotion.infrastructure.redis;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import table.eat.now.promotion.helper.IntegrationTestSupport;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
class PromotionRedisRepositoryImplTest extends IntegrationTestSupport {

  @Autowired
  private PromotionRedisRepository promotionRedisRepository;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  private final String promotionName = "대규모_이벤트_테스트";
  private String promotionUuid;

  @BeforeEach
  void setUp() {
    promotionUuid = UUID.randomUUID().toString();
  }

  @Test
  @DisplayName("인원 제한보다 적게 참여 시 SUCCESS 반환")
  void addUserToPromotion_success() {
    // given
    PromotionParticipant participant = new PromotionParticipant(1L, promotionUuid, promotionName);

    // when
    ParticipateResult result = promotionRedisRepository.addUserToPromotion(participant, 1000);

    // then
    assertThat(result).isEqualTo(ParticipateResult.SUCCESS);
  }

  @Test
  @DisplayName("정확히 1000번째 참여 시 SUCCESS_SEND_BATCH 반환")
  void addUserToPromotion_success_send_batch() {
    // given
    int max = 1000;
    for (long i = 1; i < max; i++) {
      PromotionParticipant p = new PromotionParticipant(i, promotionUuid, promotionName);
      promotionRedisRepository.addUserToPromotion(p, max);
    }

    // when
    PromotionParticipant last = new PromotionParticipant(1000L, promotionUuid, promotionName);
    ParticipateResult result = promotionRedisRepository.addUserToPromotion(last, max);

    // then
    assertThat(result).isEqualTo(ParticipateResult.SUCCESS_SEND_BATCH);
  }

  @Test
  @DisplayName("정원 초과 시 FAIL 반환")
  void addUserToPromotion_fail_exceed_limit() {
    // given
    int max = 5;
    for (long i = 1; i <= max; i++) {
      PromotionParticipant p = new PromotionParticipant(i, promotionUuid, promotionName);
      promotionRedisRepository.addUserToPromotion(p, max);
    }

    // when
    PromotionParticipant overflow = new PromotionParticipant(6L, promotionUuid, promotionName);
    ParticipateResult result = promotionRedisRepository.addUserToPromotion(overflow, max);

    // then
    assertThat(result).isEqualTo(ParticipateResult.FAIL);
  }

  @Test
  @DisplayName("프로모션 참여자 max에 따라 정원 수 정확히 저장")
  void addUserToPromotion_concurrent_control() throws InterruptedException {
    // given
    int threadCount = 100;
    int max = 50;
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // when
    for (int i = 0; i < threadCount; i++) {
      final long userId = i + 1;
      executor.submit(() -> {
        PromotionParticipant participant =
            new PromotionParticipant(userId, promotionUuid, promotionName);
        promotionRedisRepository.addUserToPromotion(participant, max);
        latch.countDown();
      });
    }

    latch.await();

    // then
    Long finalCount = getRedisZSetCount("promotion:" + promotionName);
    assertThat(finalCount).isEqualTo((long) max);
  }

  private Long getRedisZSetCount(String key) {
    return redisTemplate.opsForZSet().size(key);
  }
}