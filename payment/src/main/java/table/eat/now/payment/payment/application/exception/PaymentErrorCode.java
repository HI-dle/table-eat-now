package table.eat.now.payment.payment.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
public enum PaymentErrorCode implements ErrorCode {

  PAYMENT_NOT_FOUND("해당 결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  RESERVATION_NOT_FOUND("존재하지 않는 예약입니다.", HttpStatus.NOT_FOUND),
  PAYMENT_AMOUNT_MISMATCH("결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  RESERVATION_NOT_PENDING("결제 대기 상태의 예약만 결제할 수 있습니다.", HttpStatus.BAD_REQUEST),
  PAYMENT_APPROVAL_FAILED("결제 승인에 실패했습니다.", HttpStatus.BAD_REQUEST),
  PAYMENT_ACCESS_DENIED("해당 결제 정보를 조회할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  CANCEL_AMOUNT_EXCEED_BALANCE("취소 금액은 잔액보다 클 수 없습니다.", HttpStatus.CONFLICT),
  PAYMENT_ALREADY_CANCELLED("이미 취소된 결제입니다.", HttpStatus.CONFLICT),
  ;

  private final String message;
  private final HttpStatus status;

  PaymentErrorCode(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
}