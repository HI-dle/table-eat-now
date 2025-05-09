package table.eat.now.coupon.coupon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static table.eat.now.coupon.coupon.infrastructure.persistence.redis.constant.CouponCacheConstant.DAILY_ISSUABLE_HOT_COUPON_INDEX;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.infrastructure.persistence.redis.RedisCouponCacheManager;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class PrepareDailyCouponCacheUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private RedisCouponCacheManager redisCouponCacheManager;
  @Autowired
  private CouponReader couponReader;
  @Autowired
  private CouponStore couponStore;
  @Autowired
  private PrepareDailyCouponCacheUsecase prepareDailyCouponCacheUsecase;

  private List<Coupon> coupons;

  @BeforeEach
  void setUp() {
    coupons = CouponFixture.createCoupons(20);
    coupons.forEach(coupon -> {
      ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().plusDays(1));
      ReflectionTestUtils.setField(coupon, "label", CouponLabel.HOT);
    });
    couponStore.saveAll(coupons);
  }

  @DisplayName("그날의 핫/프로모션 쿠폰 캐싱 수행: 캐시 데이터 검증 - 성공")
  @Test
  void execute() {
    // given

    // when
    prepareDailyCouponCacheUsecase.execute();

    // then
    List<Coupon> couponsCacheBy = redisCouponCacheManager.getCouponsCacheBy(
        DAILY_ISSUABLE_HOT_COUPON_INDEX + TimeProvider.getToday());

    assertThat(couponsCacheBy).hasSize(coupons.size());
    assertThat(couponsCacheBy.get(0).getCouponUuid()).isIn(coupons.stream().map(Coupon::getCouponUuid).toList());
  }
}