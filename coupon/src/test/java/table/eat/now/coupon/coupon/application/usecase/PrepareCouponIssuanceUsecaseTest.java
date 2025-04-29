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
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class PrepareCouponIssuanceUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private PrepareCouponIssuanceUsecase prepareCouponIssuanceUsecase;

  @Autowired
  private CouponReader couponReader;

  @Autowired
  private CouponStore couponStore;

  private List<Coupon> coupons;
  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupons = CouponFixture.createCoupons(1);
    couponStore.saveAll(coupons);

    coupon = coupons.get(0);
    ReflectionTestUtils.setField(coupon.getPeriod(),
        "issueStartAt", LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1).plusMinutes(5));
    ReflectionTestUtils.setField(coupon, "label", CouponLabel.HOT);
    coupon = couponStore.save(coupon);
  }

  @DisplayName("중복 불가/한정 수량/발행 1시간 ~ 2시간 전인 쿠폰 정보들을 미리 레디스에 세팅 - 성공")
  @Test
  void execute() {
    // given

    // when
    prepareCouponIssuanceUsecase.execute();

    //then
    Long remainder = couponStore.decreaseCouponCount(coupon.getCouponUuid());
    assertThat(remainder).isEqualTo(coupon.getCount()-1);
  }

  @DisplayName("무제한/발행 1시간 ~ 2시간 전인 쿠폰 정보들은 캐싱 처리 생략: 캐싱 재고 차감 시도 - 실패")
  @Test
  void executeWithDoNothing() {
    // given
    ReflectionTestUtils.setField(coupon, "count", 0);
    ReflectionTestUtils.setField(coupon, "allowDuplicate", true);
    couponStore.save(coupon);

    // when
    prepareCouponIssuanceUsecase.execute();

    //then
    assertThat(couponReader.getCouponCount(coupon.getCouponUuid())).isEqualTo(null);
  }
}