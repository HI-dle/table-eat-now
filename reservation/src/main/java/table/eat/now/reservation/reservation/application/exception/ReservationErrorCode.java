/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

  /**
   * BAD_REQUEST
  */
  // BAD_REQUEST - AMOUNT`
  INVALID_TOTAL_AMOUNT("결제 금액의 총합이 메뉴 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  PAYMENT_LIMIT_EXCEEDED("결제는 하나여야 합니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - RESTAURANT
  INVALID_TIMESLOT("선택한 타임슬롯이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_RESERVATION_DATE("선택한 타임슬롯의 날짜가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_RESERVATION_TIME("선택한 타임슬롯의 시간이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  EXCEEDS_MAX_GUEST_CAPACITY("예약 인원이 식당 수용 가능 인원을 초과했습니다.", HttpStatus.BAD_REQUEST),
  INVALID_MENU_SELECTION("선택한 메뉴가 식당 정보와 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - DISCOUNT
  DISCOUNT_STRATEGY_NOT_FOUND("할인 전략이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - DISCOUNT - COUPON
  COUPON_NOT_FOUND("유효하지 않은 쿠폰입니다.", HttpStatus.BAD_REQUEST),
  COUPON_INVALID_PERIOD("쿠폰 사용 기간이 아닙니다.", HttpStatus.BAD_REQUEST),
  COUPON_MIN_PURCHASE_NOT_MET("쿠폰 사용을 위한 최소 구매 금액이 충족되지 않았습니다.", HttpStatus.BAD_REQUEST),
  COUPON_TYPE_NOT_FOUND("알 수 없는 쿠폰 타입입니다.", HttpStatus.BAD_REQUEST),
  INVALID_COUPON_DISCOUNT("적용된 쿠폰 할인 금액이 실제 할인 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  COUPON_USAGE_LIMIT_EXCEEDED("쿠폰 최대 사용 개수를 초과합니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - DISCOUNT - PROMOTION
  PROMOTION_NOT_FOUND("유효하지 않은 프로모션입니다.", HttpStatus.BAD_REQUEST),
  PROMOTION_INVALID_PERIOD("프로모션 적용 기간이 아닙니다.", HttpStatus.BAD_REQUEST),
  PROMOTION_INVALID_RUNNING("진행중인 프로모션이 아닙니다.", HttpStatus.BAD_REQUEST),
  INVALID_PROMOTION_DISCOUNT("적용된 프로모션 할인 금액이 실제 할인 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  PROMOTION_USAGE_LIMIT_EXCEEDED("프로모션 최대 사용 개수를 초과합니다.", HttpStatus.BAD_REQUEST),

  /**
   * INTERNAL_SERVER_ERROR
   */
  RESERVATION_SAVE_FAILED("예약 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String message;
  private final HttpStatus status;
}

