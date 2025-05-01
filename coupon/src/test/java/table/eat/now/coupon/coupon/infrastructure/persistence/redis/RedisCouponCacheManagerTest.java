package table.eat.now.coupon.coupon.infrastructure.persistence.redis;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

@Slf4j
@Disabled("수동으로 실행할 때만 @Disabled 주석을 제거하세요. CI/CD 파이프라인에서 실행되지 않도록 합니다.")
class RedisCouponCacheManagerTest extends IntegrationTestSupport {
  @Autowired
  private RedisCouponCacheManager redisCouponCacheManager;

  private List<Coupon> coupons;

  private Coupon coupon;

  private List<CouponCachingAndIndexing> couponDto;

  @BeforeEach
  void setUp() {
    coupons = CouponFixture.createCoupons(10000);
    coupon = coupons.get(0);

    couponDto = coupons.stream()
        .map(CouponCachingAndIndexing::from)
        .toList();
  }

  @DisplayName("쿠폰 10000장 캐시 등록 - 레디스 템플릿 활용한 반복 작업 수행")
  @Test
  void putCouponCache() {
    // when
    long start = System.nanoTime();
    for (Coupon coupon : coupons) {
      redisCouponCacheManager.putCouponCache(UUID.randomUUID().toString(), coupon);
    }
    long end = System.nanoTime();
    log.info("수행 시간(ms): {}", (end - start) / 1_000_000);

  }

  @DisplayName("쿠폰 10000장 캐시 등록 - 레디스 파이프라인 활용한 대량 인서트 작업 수행")
  @Test
  void pipelinedPutCouponsCache() {

    long start = System.nanoTime();
    redisCouponCacheManager.pipelinedPutCouponsCache(couponDto);
    long end = System.nanoTime();
    log.info("수행 시간(ms): {}", (end - start) / 1_000_000);
  }
}