package table.eat.now.coupon.user_coupon.application.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.dto.event.IssueUserCouponEvent;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserCouponEventListener {
  private final UserCouponService userCouponService;

  @Async("async-listener")
  @EventListener
  public void listenIssueUserCouponEvent(IssueUserCouponEvent event) {

    userCouponService.createUserCoupon(event.toCommand());
  }
}
