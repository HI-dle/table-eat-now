package table.eat.now.coupon.user_coupon.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import table.eat.now.coupon.user_coupon.application.client.dto.response.GetCouponInfoI;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfo;
import table.eat.now.coupon.user_coupon.application.dto.response.GetUserCouponInfoI;
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
  class preemptUserCoupons {

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
      userCouponService.preemptUserCoupons(userInfo, command);

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
            userCouponService.preemptUserCoupons(userInfo, command);
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

      assertThat(preemptCoupon.getStatus()).isEqualTo(UserCouponStatus.PREEMPT);
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
  class preemptUserCouponsWithDistributedLock {

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
          .isThrownBy(() -> userCouponService.preemptUserCouponsWithDistributedLock(userInfo, command));

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
            userCouponService.preemptUserCouponsWithDistributedLock(userInfo, command);
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

      assertThat(preemptCoupon.getStatus()).isEqualTo(UserCouponStatus.PREEMPT);
      assertThat(errorCount.get()).isEqualTo(9);
    }
  }

  @DisplayName("사용자 쿠폰 사용 취소 검증 - 예약 취소 이벤트 처리시 동작")
  @Nested
  class cancelUserCoupons {

    @DisplayName("성공")
    @Test
    void success() {
      // given
      String reservationUuid = UUID.randomUUID().toString();
      userCoupon.preempt(reservationUuid);
      userCouponRepository.save(userCoupon);

      // when, then
      assertThatNoException().isThrownBy(() -> userCouponService.cancelUserCoupons(reservationUuid));

      UserCoupon modified =
          userCouponRepository.findByUserCouponUuidAndDeletedAtIsNull(userCoupon.getUserCouponUuid())
          .orElseThrow(() ->  CustomException.from(UserCouponErrorCode.INVALID_USER_COUPON_UUID));
      assertThat(modified.getPreemptAt()).isNull();
      assertThat(modified.getStatus()).isEqualTo(UserCouponStatus.ROLLBACK);
    }
  }

  @DisplayName("사용자 쿠폰 목록 내부 조회 검증 - 쿠폰 메타 정보도 함께 제공됨")
  @Nested
  class getUserCouponsInternalBy {

    @DisplayName("조회 성공")
    @Test
    void success() {
      // given
      Set<String> userCouponUuids = Set.of(userCoupons.get(0).getUserCouponUuid(), userCoupons.get(1).getUserCouponUuid());

      List<String> couponUuids = List.of(userCoupons.get(0).getCouponUuid(), userCoupons.get(1).getCouponUuid());
      Map<String, GetCouponInfoI> couponInfoIMap = Map.of(
          couponUuids.get(0),
          GetCouponInfoI.builder()
              .couponId(123L)
              .couponUuid(couponUuids.get(0))
              .type("FIXED_DISCOUNT")
              .label("PROMOTION")
              .allowDuplicate(false)
              .count(1000)
              .issueStartAt(LocalDate.now().atStartOfDay())
              .issueEndAt(LocalDate.now().plusDays(8).atStartOfDay())
              .amount(1000)
              .minPurchaseAmount(10000)
              .build(),
          couponUuids.get(1),
          GetCouponInfoI.builder()
              .couponId(124L)
              .couponUuid(couponUuids.get(1))
              .type("FIXED_DISCOUNT")
              .label("PROMOTION")
              .allowDuplicate(false)
              .count(1000)
              .issueStartAt(LocalDate.now().atStartOfDay())
              .issueEndAt(LocalDate.now().plusDays(8).atStartOfDay())
              .amount(1000)
              .minPurchaseAmount(10000)
              .build()
      );

      // when
      given(couponClient.getCouponsByCouponUuids(new HashSet<>(couponUuids))).willReturn(couponInfoIMap);
      List<GetUserCouponInfoI> userCouponsInfos = userCouponService.getUserCouponsInternalBy(userCouponUuids);

      // then
      assertThat(userCouponsInfos).hasSize(2);
      assertThat(userCouponsInfos.get(0).coupon().name()).isEqualTo(couponInfoIMap.get(couponUuids.get(0)).name());
    }
  }
}