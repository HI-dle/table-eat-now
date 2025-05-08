package table.eat.now.coupon.coupon.application.aop.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import table.eat.now.coupon.global.metric.MetricInfo;

@Getter
@RequiredArgsConstructor
public enum CouponMetricInfo implements MetricInfo {
  ;

  private final String name;
  private final boolean isBatch;
}
