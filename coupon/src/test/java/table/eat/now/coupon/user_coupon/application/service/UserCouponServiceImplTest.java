package table.eat.now.coupon.user_coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.exception.UserCouponErrorCode;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.entity.UserCouponStatus;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;

class UserCouponServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private UserCouponService userCouponService;

  @Autowired
  private UserCouponRepository userCouponRepository;

  @BeforeEach
  void setUp() {
  }

  @Test
  void createUserCoupon() {
    // given
    String couponUuid = UUID.randomUUID().toString();
    String userCouponUuid = UUID.randomUUID().toString();
    IssueUserCouponCommand command = IssueUserCouponCommand.builder()
        .couponUuid(couponUuid)
        .userCouponUuid(userCouponUuid)
        .userId(2L)
        .name("4월 정기할인쿠폰")
        .expiresAt(LocalDateTime.of(2025, 5, 1, 0, 0))
        .build();

    // when
    userCouponService.createUserCoupon(command);

    // then
    UserCoupon userCoupon =
        userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
        .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

    assertThat(userCoupon.getCouponUuid()).isEqualTo(couponUuid);
    assertThat(userCoupon.getUserCouponUuid()).isEqualTo(userCouponUuid);
    assertThat(userCoupon).extracting("userId", "name", "status")
        .containsExactly(2L, "4월 정기할인쿠폰", UserCouponStatus.ISSUED);
  }
}