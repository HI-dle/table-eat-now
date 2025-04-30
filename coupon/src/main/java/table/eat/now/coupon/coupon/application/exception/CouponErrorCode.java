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


  INVALID_ISSUE_PERIOD(
      "해당 쿠폰의 발급 기간이 아닙니다.", HttpStatus.UNPROCESSABLE_ENTITY),
  INVALID_SORT_CONDITION(
      "유효하지 않은 정렬 기준입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
  LOCK_PROBLEM(
      "쿠폰 사용을 위한 잠금키에 문제가 발생하였습니다.", HttpStatus.UNPROCESSABLE_ENTITY),


  IS_OUTDATED_DATA(
      "먼저 업데이트 된 데이터와 충돌이 발생하였습니다.", HttpStatus.CONFLICT),
  ALREADY_ISSUED(
      "이미 발급받은 쿠폰을 중복 발급할 수 없습니다.", HttpStatus.CONFLICT),
  INSUFFICIENT_STOCK(
      "쿠폰 발급 수량 제한을 초과하였습니다.", HttpStatus.CONFLICT),


  NON_EXIST_STRATEGY(
      "존재할 수 없는 쿠폰 정보 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  NON_LOCK_KEY(
      "쿠폰 사용을 위한 잠금키 정보가 부정확합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_TRANSACTION_WITH_LOCK(
      "기존 트랜잭션이 존재하지 않은 경우에만 Lock을 사용할 수 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  FAILED_ROLLBACK_COUNT(
      "중복 발급으로 인한 쿠폰 수량 롤백 작업이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String message;
  private final HttpStatus status;
}
