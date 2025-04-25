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

class IssueLimitedHotStrategyTest extends IntegrationTestSupport {

  @Autowired
  private IssueLimitedHotStrategy issueLimitedHotStrategy;

  @Autowired
  private CouponRepository couponRepository;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", "GENERAL",1, true, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponRepository.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getIssueEndAt())
        .plusMinutes(10);
    couponRepository.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponRepository.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
  }

  @DisplayName("수량 제한 쿠폰 발급 전략이 발급 전략 별명을 잘 반환하는지 확인 - 성공")
  @Test
  void support() {
    // given, when
    IssueStrategyAlias alias = issueLimitedHotStrategy.alias();

    // then
    assertThat(alias).isEqualTo(IssueStrategyAlias.HOT_LIMITED);
  }

  @DisplayName("수량 제한 쿠폰을 제한 수량만큼 잘 발급하는지 확인 - 수량 초과 발급 시도시 예외 발생")
  @Test
  void issue() {
    // given
    issueLimitedHotStrategy.requestIssue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThatThrownBy(() -> issueLimitedHotStrategy.requestIssue(coupon.getCouponUuid(), 2L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.INSUFFICIENT_STOCK.getMessage());
  }
}