package table.eat.now.coupon.user_coupon.application.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserCouponErrorCode implements ErrorCode {
  INVALID_USER_COUPON_UUID(
      "존재하지 않는 사용자 쿠폰 아이디입니다.", HttpStatus.NOT_FOUND),
  NON_LOCK_KEY(
      "쿠폰 사용을 위한 잠금키 정보가 부정확합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_TRANSACTION_WITH_LOCK(
      "기존 트랜잭션이 존재하지 않은 경우에만 Lock을 사용할 수 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String message;
  private final HttpStatus status;
}
