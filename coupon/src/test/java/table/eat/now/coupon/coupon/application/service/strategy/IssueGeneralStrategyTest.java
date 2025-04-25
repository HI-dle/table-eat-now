package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class IssueGeneralStrategyTest  extends IntegrationTestSupport {

  @Autowired
  IssueGeneralStrategy strategy;

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

  @DisplayName("기본 쿠폰 발급 전략이 발급 전략 별명을 잘 반환하는지 확인 - 성공")
  @Test
  void alias() {
    // given, when
    IssueStrategyAlias alias = strategy.alias();

    // then
    assertThat(alias).isEqualTo(IssueStrategyAlias.GENERAL_BASE);
  }

  @DisplayName("쿠폰을 전략대로 발급하는지 확인 - 아무 제약 없음 / 성공")
  @Test
  void issue() {
    // given
    strategy.requestIssue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThatNoException().isThrownBy(() -> strategy.requestIssue(coupon.getCouponUuid(), 2L));
  }
}