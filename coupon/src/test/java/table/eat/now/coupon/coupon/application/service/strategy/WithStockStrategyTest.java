package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class WithStockStrategyTest extends IntegrationTestSupport {

  @Autowired
  private WithStockStrategy withStockStrategy;

  @Autowired
  private CouponRepository couponRepository;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", "HOT",1, true, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "startAt", LocalDateTime.now().minusDays(1));
    couponRepository.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getEndAt())
        .plusMinutes(10);
    couponRepository.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponRepository.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
  }

  @DisplayName("수량 제한 쿠폰을 수량 제한 쿠폰 발급 전략이 지원하는지 확인 - 성공")
  @Test
  void support() {
    // given, when
    boolean support = withStockStrategy.support(coupon);

    // then
    assertThat(support).isTrue();
  }

  @DisplayName("수량 제한 쿠폰을 제한 수량만큼 잘 발급하는지 확인 - 수량 초과 발급 시도시 예외 발생")
  @Test
  void issue() {
    // given
    withStockStrategy.requestIssue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThatThrownBy(() -> withStockStrategy.requestIssue(coupon.getCouponUuid(), 2L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.INSUFFICIENT_STOCK.getMessage());
  }
}