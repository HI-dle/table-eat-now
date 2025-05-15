package table.eat.now.coupon.coupon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.COUPON_CACHE;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DIRTY_COUPON_ZSET;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.command.CouponIssuance;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.infrastructure.persistence.jpa.JpaCouponRepository;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class SyncCouponCacheToDbUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private SyncCouponCacheToDbUsecase syncCouponCacheToDbUsecase;

  @Autowired
  private CouponReader couponReader;
  @Autowired
  private CouponStore couponStore;
  @Autowired
  private JpaCouponRepository jpaCouponRepository;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    // 핫 쿠폰
    coupon = CouponFixture.createHotCoupon(
        1, "FIXED_DISCOUNT", 2, false, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration cacheDuration = TimeProvider.getDuration(coupon.calcExpireAt(), 60);
    couponStore.insertCouponCache(coupon.getCouponUuid(), coupon, cacheDuration);
  }

  @DisplayName("변경 감지된 쿠폰 캐시: 디비 싱크 수행 검증 - 성공")
  @Transactional
  @Test
  void execute() {
    // given
    couponStore.requestIssue(CouponIssuance.builder()
        .couponUuid(coupon.getCouponUuid())
        .timestamp(TimeProvider.getEpochMillis(LocalDateTime.now()))
        .couponProfile(CouponProfile.parse(coupon))
        .userId(1L)
        .build());

    Double result = stringRedisTemplate.opsForZSet().incrementScore(
        DIRTY_COUPON_ZSET,
        COUPON_CACHE + coupon.getCouponUuid(),
        -6 * 60_000L);

    // when
    syncCouponCacheToDbUsecase.execute();

    // then
    entityManager.clear();
    Coupon updated = jpaCouponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(
            coupon.getCouponUuid())
        .orElseThrow(() -> new RuntimeException("coupon not found"));

    assertThat(updated.getIssuedCount()).isEqualTo(1);

    Double score = stringRedisTemplate.opsForZSet()
        .score(DIRTY_COUPON_ZSET, COUPON_CACHE + coupon.getCouponUuid());
    assertThat(score).isNull();
  }
}