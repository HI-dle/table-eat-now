package table.eat.now.coupon.coupon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.messaging.EventPublisher;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.coupon.application.usecase.dto.request.IssuePromotionCouponCommand;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.infrastructure.exception.CouponInfraErrorCode;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class IssuePromotionCouponUsecaseTest extends IntegrationTestSupport {

  @Autowired
  IssuePromotionCouponUsecase issuePromotionCouponUsecase;

  @Autowired
  private CouponReader couponReader;

  @Autowired
  private CouponStore couponStore;

  @MockitoBean
  private EventPublisher<CouponRequestedIssueEvent> eventPublisher;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", "PROMOTION",2, false, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = TimeProvider.getDuration(coupon.getPeriod().getIssueEndAt(), 60);
    Duration cacheDuration = TimeProvider.getDuration(coupon.calcExpireAt(), 60);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
    couponStore.insertCouponCache(coupon.getCouponUuid(), coupon, cacheDuration);
  }

  @DisplayName("프로모션 쿠폰 발급 이벤트 처리: 검증/재고 차감 및 이벤트 발행 - 성공")
  @Test
  void success() {
    // given
    IssuePromotionCouponCommand command = IssuePromotionCouponCommand.builder()
        .couponUuid(coupon.getCouponUuid())
        .userId(1L)
        .timestamp(Instant.now().toEpochMilli())
        .build();

    // when
    issuePromotionCouponUsecase.execute(command);

    // then
    Coupon updated = couponReader.findValidCouponByUuid(coupon.getCouponUuid())
        .orElseThrow(RuntimeException::new);
    assertThat(updated.getIssuedCount()).isEqualTo(1);
    verify(eventPublisher, times(1)).publish(any(CouponRequestedIssueEvent.class));
  }

  @DisplayName("프로모션 쿠폰 발급 이벤트 처리: 동일한 유저의 재발급 요청 - 실패")
  @Test
  void failedWithRepeatedRequest() {
    // given
    IssuePromotionCouponCommand command = IssuePromotionCouponCommand.builder()
        .couponUuid(coupon.getCouponUuid())
        .userId(1L)
        .timestamp(Instant.now().toEpochMilli())
        .build();
    IssuePromotionCouponCommand command2 = IssuePromotionCouponCommand.builder()
        .couponUuid(coupon.getCouponUuid())
        .userId(1L)
        .timestamp(Instant.now().toEpochMilli() + 100) // idempotencyKey 에러 발생 방지
        .build();

    // when
    issuePromotionCouponUsecase.execute(command);

    // then
    assertThatThrownBy(() -> issuePromotionCouponUsecase.execute(command2))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponInfraErrorCode.DUPLICATED_REQUEST.getMessage());

    Coupon updated = couponReader.findValidCouponByUuid(coupon.getCouponUuid())
        .orElseThrow(RuntimeException::new);
    assertThat(updated.getIssuedCount()).isEqualTo(1);
  }

//  @DisplayName("프로모션 쿠폰 발급 이벤트 처리: 100명이 동시에 발급 요청시 100개 발급 확인 - 성공")
//  @Test
//  void successWithConcurrentRequest() {
//    // given
//    IssuePromotionCouponCommand command = IssuePromotionCouponCommand.builder()
//        .couponUuid(coupon.getCouponUuid())
//        .userId(1L)
//        .timestamp(Instant.now().toEpochMilli())
//        .build();
//    IssuePromotionCouponCommand command2 = IssuePromotionCouponCommand.builder()
//        .couponUuid(coupon.getCouponUuid())
//        .userId(1L)
//        .timestamp(Instant.now().toEpochMilli() + 100) // idempotencyKey 에러 발생 방지
//        .build();
//
//    // when
//    issuePromotionCouponUsecase.execute(command);
//
//    // then
//    assertThatThrownBy(() -> issuePromotionCouponUsecase.execute(command2))
//        .isInstanceOf(CustomException.class)
//        .hasMessage(CouponInfraErrorCode.DUPLICATED_REQUEST.getMessage());
//
//    Coupon updated = couponReader.findValidCouponByUuid(coupon.getCouponUuid())
//        .orElseThrow(RuntimeException::new);
//    assertThat(updated.getIssuedCount()).isEqualTo(1);
//  }
}