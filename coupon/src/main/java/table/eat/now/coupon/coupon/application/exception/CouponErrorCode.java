package table.eat.now.coupon.coupon.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
  INVALID_COUPON_UUID(
      "존재하지 않는 쿠폰 아이디입니다.", HttpStatus.NOT_FOUND),
  INVALID_SORT_CONDITION(
      "유효하지 않은 정렬 기준입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
  ;

  private final String message;
  private final HttpStatus status;
}
