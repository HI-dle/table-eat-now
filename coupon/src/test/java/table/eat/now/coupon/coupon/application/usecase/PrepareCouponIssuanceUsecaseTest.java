package table.eat.now.coupon.coupon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class PrepareCouponIssuanceUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private PrepareCouponIssuanceUsecase prepareCouponIssuanceUsecase;

  @Autowired
  private CouponRepository couponRepository;

  private List<Coupon> coupons;

  @BeforeEach
  void setUp() {
    coupons = CouponFixture.createCoupons(1);
    couponRepository.saveAll(coupons);

    ReflectionTestUtils.setField(coupons.get(0).getPeriod(),
        "startAt", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1).withMinute(5));
    couponRepository.save(coupons.get(0));
  }

  @DisplayName("발행 1시간 ~ 2시간 전인 쿠폰 정보들을 미리 레디스에 세팅 - 성공")
  @Test
  void execute() {
    // given
    Coupon coupon = coupons.get(0);

    // when
    prepareCouponIssuanceUsecase.execute();

    //then
    Long remainder = couponRepository.decreaseCouponCount(coupon.getCouponUuid());
    assertThat(remainder).isEqualTo(coupon.getCount()-1);
  }
}