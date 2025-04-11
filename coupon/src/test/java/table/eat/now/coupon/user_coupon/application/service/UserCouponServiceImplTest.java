package table.eat.now.coupon.user_coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.exception.UserCouponErrorCode;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.entity.UserCouponStatus;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;
import table.eat.now.coupon.user_coupon.fixture.UserCouponFixture;

@Slf4j
class UserCouponServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private UserCouponService userCouponService;

  @Autowired
  private UserCouponRepository userCouponRepository;

  private UserCoupon userCoupon;

  @BeforeEach
  void setUp() {
    userCoupon = UserCouponFixture.create(2L);
    userCouponRepository.save(userCoupon);
  }

  @DisplayName("사용자 쿠폰 생성 검증 - 생성 성공")
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

  @DisplayName("일반 사용자 쿠폰 선점 검증 - 선점 성공")
  @Test
  void preemptUserCoupon() {
    // given
    String userCouponUuid = userCoupon.getUserCouponUuid();
    String reservationUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    PreemptUserCouponCommand command = PreemptUserCouponCommand.builder()
        .reservationUuid(reservationUuid)
        .build();

    // when
    userCouponService.preemptUserCoupon(userInfo, userCouponUuid, command);

    // then
    UserCoupon userCoupon =
        userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
            .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

    assertThat(userCoupon.getReservationUuid()).isEqualTo(reservationUuid);
    assertThat(userCoupon).extracting("userId", "name", "status")
        .containsExactly(2L, "test 사용자 쿠폰", UserCouponStatus.PREEMPT);
  }

  @DisplayName("일반 사용자 쿠폰 선점 경합 발생시 하나만 성공하는 것 검증 - 선점 성공")
  @Test
  void preemptUserCouponWithRaceCondition() throws InterruptedException {
    // given
    String userCouponUuid = userCoupon.getUserCouponUuid();
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);

    int threadCount = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger errorCount = new AtomicInteger(0);

    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          PreemptUserCouponCommand command = PreemptUserCouponCommand.builder()
              .reservationUuid(UUID.randomUUID().toString())
              .build();
          userCouponService.preemptUserCoupon(userInfo, userCouponUuid, command);
        } catch (Exception e) {
          log.error(e.getMessage());
          errorCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();
    executorService.shutdown();

    // then
    UserCoupon userCoupon =
        userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
            .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

    assertThat(errorCount.get()).isEqualTo(9);
  }
}