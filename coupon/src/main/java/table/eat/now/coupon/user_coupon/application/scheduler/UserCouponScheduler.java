package table.eat.now.coupon.user_coupon.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.user_coupon.application.aop.annotation.DistributedLock;
import table.eat.now.coupon.user_coupon.application.usecase.ReleaseUserCouponUsecase;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponScheduler {
  private final ReleaseUserCouponUsecase releaseUserCouponUsecase;

  @DistributedLock(subPrefix = "schedule:realse", waitTime = 0L, leaseTime = 40L)
  @Scheduled(cron="0 * * * * *")
  public void releaseUserCoupon() {
    log.info("10분 이상 선점 상태 쿠폰 릴리즈::시작");
    releaseUserCouponUsecase.execute();
    log.info("10분 이상 선점 상태 쿠폰 릴리즈::완료");
  }
}
