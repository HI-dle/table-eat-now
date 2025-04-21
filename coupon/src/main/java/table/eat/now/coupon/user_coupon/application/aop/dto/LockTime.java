package table.eat.now.coupon.user_coupon.application.aop.dto;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.aop.annotation.DistributedLock;

@Builder
public record LockTime(
    long waitTime,
    long leaseTime,
    TimeUnit timeUnit
) {

  public static LockTime from(DistributedLock distributedLock) {
    return LockTime.builder()
        .waitTime(distributedLock.waitTime())
        .leaseTime(distributedLock.leaseTime())
        .timeUnit(distributedLock.timeUnit())
        .build();
  }
}
