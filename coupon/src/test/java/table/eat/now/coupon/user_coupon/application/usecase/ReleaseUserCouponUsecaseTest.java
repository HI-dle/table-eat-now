package table.eat.now.coupon.user_coupon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.application.exception.UserCouponErrorCode;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.entity.UserCouponStatus;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;
import table.eat.now.coupon.user_coupon.fixture.UserCouponFixture;

class ReleaseUserCouponUsecaseTest extends IntegrationTestSupport {

  @Autowired
  private ReleaseUserCouponUsecase releaseUserCouponUsecase;

  @Autowired
  private UserCouponRepository userCouponRepository;

  UserCoupon userCoupon;

  @BeforeEach
  void setUp() {
    userCoupon = UserCouponFixture.create(1, 2L);
    userCouponRepository.save(userCoupon);
  }

  @DisplayName("선점 후 10분 초과된 사용자 쿠폰을 선점 해제하는지 검증 - 성공")
  @Test
  void execute() {
    // given
    ReflectionTestUtils.setField(userCoupon, "preemptAt", LocalDateTime.now().minusMinutes(11));
    ReflectionTestUtils.setField(userCoupon, "status", UserCouponStatus.PREEMPT);
    ReflectionTestUtils.setField(userCoupon, "reservationUuid", UUID.randomUUID().toString());
    userCouponRepository.save(userCoupon);

    // when
    releaseUserCouponUsecase.execute();

    // then
    UserCoupon released = userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(
            userCoupon.getUserCouponUuid())
        .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));
    assertThat(released.getPreemptAt()).isNull();
    assertThat(released.getReservationUuid()).isNull();
    assertThat(released.getStatus()).isEqualTo(UserCouponStatus.ROLLBACK);
  }
}