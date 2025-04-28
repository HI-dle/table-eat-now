package table.eat.now.coupon.user_coupon.application.aop.decorator;

import java.util.List;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.aop.annotation.WithSimpleTransaction;
import table.eat.now.coupon.user_coupon.application.aop.dto.LockTime;

@Builder
public record UserCouponTaskCondiment(
    List<String> lockKeys,
    LockTime lockTime,
    boolean transactional,
    boolean readOnly
) {

  public static UserCouponTaskCondiment of(List<String> lockKeys, LockTime lockTime, WithSimpleTransaction withSimpleTransaction) {
    return UserCouponTaskCondiment.builder()
        .lockKeys(lockKeys)
        .lockTime(lockTime)
        .transactional(withSimpleTransaction != null)
        .readOnly(withSimpleTransaction != null && withSimpleTransaction.readOnly())
        .build();
  }
}
