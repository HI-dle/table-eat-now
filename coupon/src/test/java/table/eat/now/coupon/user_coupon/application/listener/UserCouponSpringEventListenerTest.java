package table.eat.now.coupon.user_coupon.application.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent.CouponRequestedIssuePayload;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.event.EventType;
import table.eat.now.coupon.user_coupon.infrastructure.messaging.spring.UserCouponSpringEventListener;

@ExtendWith(SpringExtension.class)
@Import({UserCouponSpringEventListener.class})
class UserCouponSpringEventListenerTest {

  @Autowired
  private UserCouponSpringEventListener userCouponSpringEventListener;

  @MockitoBean
  private UserCouponService userCouponService;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("사용자 쿠폰 발행 이벤트를 수신하고 로직을 잘 수행하는지 검증 - 성공")
  @Test
  void listenIssueUserCouponEvent() {
    // given
    String couponUuid = UUID.randomUUID().toString();
    String userCouponUuid = UUID.randomUUID().toString();
    CouponRequestedIssueEvent issueUserCouponEvent = CouponRequestedIssueEvent.builder()
        .eventType(EventType.COUPON_REQUESTED_ISSUE.name())
        .userCouponUuid(userCouponUuid)
        .payload(
            CouponRequestedIssuePayload.builder()
                .userCouponUuid(userCouponUuid)
                .couponUuid(couponUuid)
                .userId(2L)
                .name("4월 정기할인쿠폰")
                .expiresAt(LocalDateTime.of(2025, 5, 1, 0, 0))
                .build()
        )
        .build();

    doNothing().when(userCouponService).createUserCoupon(issueUserCouponEvent.toCommand());

    // when
    userCouponSpringEventListener.listen(issueUserCouponEvent);

    // then
    verify(userCouponService).createUserCoupon(issueUserCouponEvent.toCommand());
  }
}