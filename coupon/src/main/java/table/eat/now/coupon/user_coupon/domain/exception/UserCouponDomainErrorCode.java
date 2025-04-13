package table.eat.now.coupon.user_coupon.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserCouponDomainErrorCode implements ErrorCode {
  UNAUTH_USER_COUPON(
      "해당 쿠폰에 대한 사용 권한이 없습니다.", HttpStatus.FORBIDDEN),
  EXPIRED_USER_COUPON(
      "이미 만료된 쿠폰입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_USED_USER_COUPON(
      "이미 사용 완료된 쿠폰입니다.", HttpStatus.CONFLICT),
  PREEMPT_USER_COUPON(
      "이미 선점된 쿠폰입니다.", HttpStatus.CONFLICT),
  ;

  private final String message;
  private final HttpStatus status;
}
