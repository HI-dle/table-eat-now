package table.eat.now.coupon.user_coupon.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserCouponErrorCode implements ErrorCode {
  INVALID_USER_COUPON_UUID(
      "존재하지 않는 사용자 쿠폰 아이디입니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus status;
}
