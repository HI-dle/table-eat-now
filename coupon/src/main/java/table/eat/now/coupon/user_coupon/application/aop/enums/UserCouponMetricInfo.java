package table.eat.now.coupon.user_coupon.application.aop.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import table.eat.now.coupon.global.metric.MetricInfo;

@Getter
@RequiredArgsConstructor
public enum UserCouponMetricInfo implements MetricInfo {

  USER_COUPON_CONSUME_REQUESTED_BATCH("user.coupon", true),
  USER_COUPON_CONSUME_REQUESTED_ONE("user.coupon", false),
  ;

  private final String name;
  private final boolean isBatch;
}
