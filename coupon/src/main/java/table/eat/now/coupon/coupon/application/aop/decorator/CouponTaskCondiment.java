package table.eat.now.coupon.coupon.application.aop.decorator;

import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.coupon.application.aop.annotation.WithSimpleTransaction;
import table.eat.now.coupon.coupon.application.aop.dto.LockTime;

@Builder
public record CouponTaskCondiment(
    List<String> lockKeys,
    LockTime lockTime,
    boolean transactional,
    boolean readOnly
) {

  public static CouponTaskCondiment of(List<String> lockKeys, LockTime lockTime, WithSimpleTransaction withSimpleTransaction) {
    return CouponTaskCondiment.builder()
        .lockKeys(lockKeys)
        .lockTime(lockTime)
        .transactional(withSimpleTransaction != null)
        .readOnly(withSimpleTransaction != null && withSimpleTransaction.readOnly())
        .build();
  }
}
