package table.eat.now.coupon.user_coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.dto.response.PageResponse;
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

  private List<UserCoupon> userCoupons;

  private UserCoupon userCoupon;

  @BeforeEach
  void setUp() {
    userCoupons = UserCouponFixture.createList(20, 2L);
    userCouponRepository.saveAll(userCoupons);
    userCoupon = userCoupons.get(0);
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

  @DisplayName("일반 사용자 쿠폰 선점시 비관락 적용한 경우 검증")
  @Nested
  class preemptUserCoupon {

    @DisplayName("선점 성공")
    @Test
    void success() {
      // given
      String userCouponUuid = userCoupon.getUserCouponUuid();
      String reservationUuid = UUID.randomUUID().toString();
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      PreemptUserCouponCommand command = PreemptUserCouponCommand.builder()
          .reservationUuid(reservationUuid)
          .userCouponUuids(Set.of(userCouponUuid, userCoupons.get(1).getUserCouponUuid()))
          .build();

      // when
      userCouponService.preemptUserCoupon(userInfo, command);

      // then
      UserCoupon userCoupon =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
              .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

      assertThat(userCoupon.getReservationUuid()).isEqualTo(reservationUuid);
      assertThat(userCoupon).extracting("userId", "name", "status")
          .containsExactly(2L, "test 사용자 쿠폰 0", UserCouponStatus.PREEMPT);
    }

    @DisplayName("쿠폰 선점 경합 발생시 하나만 성공하는 것 검증 - 성공")
    @Test
    void successWhenRaceCondition() throws InterruptedException {
      // given
      String userCouponUuid = userCoupon.getUserCouponUuid();
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);

      int threadCount = 10;
      ExecutorService executorService = Executors.newFixedThreadPool(32);
      CountDownLatch latch = new CountDownLatch(threadCount);
      AtomicInteger errorCount = new AtomicInteger(0);

      // when
      for (int i = 0; i < threadCount; i++) {
        var userCouponUuid2 = userCoupons.get(i + 1).getUserCouponUuid();
        executorService.submit(() -> {
          try {
            PreemptUserCouponCommand command = PreemptUserCouponCommand.builder()
                .reservationUuid(UUID.randomUUID().toString())
                .userCouponUuids(Set.of(userCouponUuid, userCouponUuid2))
                .build();
            userCouponService.preemptUserCoupon(userInfo, command);
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
      UserCoupon preemptCoupon =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
              .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));
      UserCoupon failedToPreemptCoupon =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCoupons.get(2).getUserCouponUuid())
              .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

      assertThat(preemptCoupon.getStatus()).isEqualTo(UserCouponStatus.PREEMPT);
      assertThat(failedToPreemptCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
      assertThat(errorCount.get()).isEqualTo(9);
    }
  }

  @DisplayName("사용자별 쿠폰 조회 검증 - 성공")
  @Test
  void getUserCouponsByUserId() {

    // given
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
    Pageable pageable = PageRequest.of(0, 30, Sort.by("expiresAt").ascending());

    // when
    PageResponse<GetUserCouponInfo> getUserCoupons = userCouponService.getUserCouponsByUserId(
        userInfo, pageable);

    assertThat(getUserCoupons.contents().get(0).userId()).isEqualTo(2L);
    assertThat(getUserCoupons.totalElements()).isEqualTo(20);
    assertThat(getUserCoupons.pageSize()).isEqualTo(pageable.getPageSize());
  }

  @DisplayName("일반 사용자 쿠폰 선점시 분산락 적용한 경우 검증")
  @Nested
  class preemptUserCouponWithDistributedLock {

    @DisplayName("선점 성공")
    @Test
    void success() {
      // given
      String userCouponUuid = userCoupon.getUserCouponUuid();
      String reservationUuid = UUID.randomUUID().toString();
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      PreemptUserCouponCommand command = PreemptUserCouponCommand.builder()
          .reservationUuid(reservationUuid)
          .userCouponUuids(Set.of(userCouponUuid, userCoupons.get(1).getUserCouponUuid()))
          .build();

      // when, then
      assertThatNoException()
          .isThrownBy(() -> userCouponService.preemptUserCouponWithDistributedLock(userInfo, command));

      UserCoupon userCoupon =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
              .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

      assertThat(userCoupon.getReservationUuid()).isEqualTo(reservationUuid);
      assertThat(userCoupon).extracting("userId", "name", "status")
          .containsExactly(2L, "test 사용자 쿠폰 0", UserCouponStatus.PREEMPT);
    }

    @DisplayName("쿠폰 선점 경합 발생시 하나만 성공하는 것 검증 - 성공")
    @Test
    void successWhenRaceCondition() throws InterruptedException {
      // given
      String userCouponUuid = userCoupon.getUserCouponUuid();
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);

      int threadCount = 10;
      ExecutorService executorService = Executors.newFixedThreadPool(32);
      CountDownLatch latch = new CountDownLatch(threadCount);
      AtomicInteger errorCount = new AtomicInteger(0);

      // when
      for (int i = 0; i < threadCount; i++) {
        var userCouponUuid2 = userCoupons.get(i + 1).getUserCouponUuid();
        executorService.submit(() -> {
          try {
            PreemptUserCouponCommand command = PreemptUserCouponCommand.builder()
                .reservationUuid(UUID.randomUUID().toString())
                .userCouponUuids(Set.of(userCouponUuid, userCouponUuid2))
                .build();
            userCouponService.preemptUserCouponWithDistributedLock(userInfo, command);
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
      UserCoupon preemptCoupon =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCouponUuid)
              .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));
      UserCoupon failedToPreemptCoupon =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCoupons.get(2).getUserCouponUuid())
              .orElseThrow(() -> CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));

      assertThat(preemptCoupon.getStatus()).isEqualTo(UserCouponStatus.PREEMPT);
      assertThat(failedToPreemptCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
      assertThat(errorCount.get()).isEqualTo(9);
    }
  }
}