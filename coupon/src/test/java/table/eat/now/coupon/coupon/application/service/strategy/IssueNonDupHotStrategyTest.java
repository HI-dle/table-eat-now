package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
import table.eat.now.coupon.coupon.application.strategy.IssueNonDupHotStrategy;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class IssueNonDupHotStrategyTest extends IntegrationTestSupport {

  @Autowired
  private IssueNonDupHotStrategy issueNonDupHotStrategy;

  @Autowired
  private CouponReader couponReader;

  @Autowired
  private CouponStore couponStore;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", "GENERAL", 0, false, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getIssueEndAt())
        .plusMinutes(10);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
  }

  @DisplayName("중복 발급 제한 쿠폰 발급 전략이 발급 전략 별명을 잘 반환하는지 확인 - 성공")
  @Test
  void alias() {
    // given, when
    CouponProfile alias = issueNonDupHotStrategy.couponProfile();

    // then
    assertThat(alias).isEqualTo(CouponProfile.HOT_NONDUP);
  }

  @DisplayName("중복 발급 제한 쿠폰을 중복 없이 발급하는지 확인 - 중복 발급 시도시 예외 발생")
  @Test
  void issue() {
    // given
    assertThatCode(() -> issueNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L))
        .doesNotThrowAnyException();

    // when, then
    assertThatThrownBy(() -> issueNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.ALREADY_ISSUED.getMessage());
  }
}