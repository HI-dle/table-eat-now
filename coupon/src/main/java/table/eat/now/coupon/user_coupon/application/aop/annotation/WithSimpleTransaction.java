package table.eat.now.coupon.user_coupon.application.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithSimpleTransaction {

  /**
   * 락의 실행 단위 트랜잭션 읽기 속성
   */
  boolean readOnly() default true;
}
