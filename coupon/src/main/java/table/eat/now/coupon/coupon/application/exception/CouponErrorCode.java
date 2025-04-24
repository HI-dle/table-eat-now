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

  ALREADY_ISSUED(
      "이미 발급받은 쿠폰을 중복 발급할 수 없습니다.", HttpStatus.CONFLICT),
  INSUFFICIENT_STOCK(
      "쿠폰 발급 수량 제한을 초과하였습니다.", HttpStatus.CONFLICT),

  FAILED_ROLLBACK_COUNT(
      "중복 발급으로 인한 쿠폰 수량 롤백 작업이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  NON_EXIST_STRATEGY(
      "존재할 수 없는 쿠폰 정보 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);


  private final String message;
  private final HttpStatus status;
}
