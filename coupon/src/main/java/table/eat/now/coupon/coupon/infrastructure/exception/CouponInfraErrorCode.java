package table.eat.now.coupon.coupon.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CouponInfraErrorCode implements ErrorCode {

  REQUESTED_ISSUE_FAILED(
      "쿠폰 발급 시도가 실패하였습니다.",
      HttpStatus.UNPROCESSABLE_ENTITY),
  IDEMPOTENCY_ERROR(
      "이벤트 중복 처리 시도로 오류 발생하였습니다.",
      HttpStatus.CONFLICT),
  DUPLICATED_REQUEST(
      "사용자가 중복 불가 쿠폰에 중복 발급을 시도하였습니다.",
      HttpStatus.BAD_GATEWAY),
  NOT_ENOUGH_STOCK(
      "쿠폰의 재고가 부족하여 발급에 실패하였습니다.",
      HttpStatus.CONFLICT),

  INVALID_COUPON_STOCK(
      "쿠폰의 재고 정보가 올바르지 않습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR),
  NOT_FOUND_LUA_SCRIPT(
      "루아 스크립트가 존재하지 않아 오류가 발생하였습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR),
  FAILED_LUA_SCRIPT(
      "루아 스크립트 실행에 실패하였습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String message;
  private final HttpStatus status;
}
