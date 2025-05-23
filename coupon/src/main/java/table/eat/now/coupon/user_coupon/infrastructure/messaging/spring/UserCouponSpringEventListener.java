package table.eat.now.coupon.user_coupon.infrastructure.messaging.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserCouponSpringEventListener {
  private final UserCouponService userCouponService;

  @Async("async-listener")
  @EventListener
  public void listen(CouponRequestedIssueEvent event) {

    userCouponService.createUserCoupon(event.toCommand());
  }
}
