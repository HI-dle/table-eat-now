package table.eat.now.coupon.user_coupon.domain.exception;

import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.type.ErrorCode;

public class UserCouponException extends CustomException {

  public UserCouponException(ErrorCode errorCode) {
    super(errorCode);
  }
}
