package table.eat.now.coupon.user_coupon.infrastructure.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserCouponInfraErrorCode implements ErrorCode {
  LOCK_PROBLEM(
      "쿠폰 사용을 위한 잠금키에 문제가 발생하였습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
  INVALID_TRANSACTION_WITH_LOCK(
      "기존 트랜잭션이 존재하지 않은 경우에만 Lock을 사용할 수 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String message;
  private final HttpStatus status;
}
