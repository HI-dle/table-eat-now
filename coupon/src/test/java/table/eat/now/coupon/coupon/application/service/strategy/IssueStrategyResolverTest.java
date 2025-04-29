package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.coupon.coupon.application.strategy.IssueGeneralStrategy;
import table.eat.now.coupon.coupon.application.strategy.IssueStrategy;
import table.eat.now.coupon.coupon.application.strategy.IssueStrategyResolver;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class IssueStrategyResolverTest extends IntegrationTestSupport {

  @Autowired
  IssueStrategyResolver resolver;

  public static Stream<Arguments> getCouponDataWithDesc() {
    return Stream.of(
        Arguments.of(
            CouponFixture.createCoupon(
            1,
            "FIXED_DISCOUNT",
            "GENERAL",
            100,
            true,
            1000,
            null,
            null
            ),
            "수량제한 및 중복 가능 쿠폰"
        ),
        Arguments.of(
            CouponFixture.createCoupon(
                1,
                "FIXED_DISCOUNT",
                "GENERAL",
                100,
                false,
                1000,
                null,
                null
            ),
            "수량 제한 및 중복 불가 쿠폰"
        ),
        Arguments.of(
            CouponFixture.createCoupon(
                1,
                "FIXED_DISCOUNT",
                "GENERAL",
                0,
                true,
                1000,
                null,
                null
            ),
            "무제한 쿠폰"
        ),
        Arguments.of(
            CouponFixture.createCoupon(
                1,
                "FIXED_DISCOUNT",
                "GENERAL",
                0,
                false,
                1000,
                null,
                null
            ),
            "중복 불가 쿠폰"
        )
    );
  }

  @MethodSource("getCouponDataWithDesc")
  @ParameterizedTest(name = "전략 리졸버를 통해서 전략 조회 검증 - {1} 쿠폰은 전략을 조회할 수 있다.")
  void resolve(Coupon coupon, String description) {
    // given

    // when
    IssueStrategy generalStrategy = resolver.resolve(coupon);

    // then
    assertThat(generalStrategy).isInstanceOf(IssueStrategy.class);
  }

  @DisplayName("전략 리졸버를 통해서 전략 조회 검증 - 주어진 쿠폰에 따라 일반 발급 전략 확인 성공")
  @Test
  void resolve() {
    // given
    Coupon coupon = CouponFixture.createCoupon(
        1,
        "FIXED_DISCOUNT",
        "GENERAL",
        0,
        true,
        1000,
        null,
        null
    );

    // when
    IssueStrategy generalStrategy = resolver.resolve(coupon);

    // then
    assertThat(generalStrategy.getClass()).isEqualTo(IssueGeneralStrategy.class);
  }
}