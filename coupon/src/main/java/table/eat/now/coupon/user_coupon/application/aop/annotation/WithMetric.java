package table.eat.now.coupon.user_coupon.application.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import table.eat.now.coupon.user_coupon.application.aop.enums.UserCouponMetricInfo;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithMetric {
  /**
   * 메트릭 정보
   */
  UserCouponMetricInfo info();
}
