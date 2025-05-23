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
  // BAD_REQUEST - CANCEL
  CANCELLATION_DEADLINE_PASSED("취소 마감 시간이 지났습니다.", HttpStatus.BAD_REQUEST),
  ALREADY_CANCELED("이미 취소가 된 상태입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_DELETED("이미 삭제가 된 상태입니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - CONFIRM
  INVALID_STATUS_FOR_CONFIRMATION("확정 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - AMOUNT`
  INVALID_MENU_TOTAL_AMOUNT("총 금액이 메뉴 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PAYMENT_DETAILS_TOTAL_AMOUNT("총 금액이결제 총 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  PAYMENT_LIMIT_EXCEEDED("결제는 하나여야 합니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - RESTAURANT
  INVALID_TIMESLOT("선택한 타임슬롯이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_RESERVATION_DATE("선택한 타임슬롯의 날짜가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_RESERVATION_TIME("선택한 타임슬롯의 시간이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  EXCEEDS_MAX_GUEST_CAPACITY("예약 인원이 식당 수용 가능 인원을 초과했습니다.", HttpStatus.BAD_REQUEST),
  INVALID_MENU_SELECTION("선택한 메뉴가 식당 정보와 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - DISCOUNT
  DISCOUNT_STRATEGY_NOT_FOUND("할인 전략이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - COUPON
  USERCOUPON_NOT_FOUND("유효하지 않은 유저 쿠폰입니다.", HttpStatus.BAD_REQUEST),
  USERCOUPON_EXPIRED("만료된 유저 쿠폰입니다.", HttpStatus.BAD_REQUEST),
  COUPON_MIN_PURCHASE_NOT_MET("쿠폰 사용을 위한 최소 구매 금액이 충족되지 않았습니다.", HttpStatus.BAD_REQUEST),
  COUPON_TYPE_NOT_FOUND("알 수 없는 쿠폰 타입입니다.", HttpStatus.BAD_REQUEST),
  INVALID_COUPON_DISCOUNT("적용된 쿠폰 할인 금액이 실제 할인 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  COUPON_USAGE_LIMIT_EXCEEDED("쿠폰 최대 사용 개수를 초과합니다.", HttpStatus.BAD_REQUEST),
  INVALID_USERCOUPON_STATUS_FOR_RESERVATION("유저 쿠폰 상태가 예약 가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),

  // BAD_REQUEST - DISCOUNT - PROMOTION
  PROMOTION_NOT_FOUND("유효하지 않은 프로모션입니다.", HttpStatus.BAD_REQUEST),
  PROMOTION_INVALID_RUNNING("진행중인 프로모션이 아닙니다.", HttpStatus.BAD_REQUEST),
  INVALID_PROMOTION_DISCOUNT("적용된 프로모션 할인 금액이 실제 할인 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  PROMOTION_EVENT_USAGE_LIMIT_EXCEEDED("프로모션 최대 사용 개수를 초과합니다.", HttpStatus.BAD_REQUEST),

  /**
   * FORBIDDEN
   */
  NO_CANCEL_PERMISSION("해당 예약에 대한 취소 권한이 없습니다.",HttpStatus.FORBIDDEN),
  COUPON_USE_PERMISSION("해당 쿠폰에 대한 사용 권한이 없습니다.",HttpStatus.FORBIDDEN),

  /**
   * NOT_FOUND
   */
  NOT_FOUND("예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  /**
   * INTERNAL_SERVER_ERROR
   */
  RESERVATION_SAVE_FAILED("예약 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String message;
  private final HttpStatus status;
}

