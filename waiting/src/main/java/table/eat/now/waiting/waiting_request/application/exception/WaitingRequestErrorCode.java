package table.eat.now.waiting.waiting_request.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum WaitingRequestErrorCode implements ErrorCode {
  ALREADY_EXISTS_WAITING(
      "이미 동일한 휴대번호로 대기 요청이 되어 있습니다.", HttpStatus.CONFLICT),
  FAILED_ENQUEUE(
      "대기열 등록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE),
  INVALID_WAITING_REQUEST_UUID(
      "유효하지 않은 대기 요청 아이디입니다.", HttpStatus.NOT_FOUND),
  UNAUTH_REQUEST(
      "권한이 없는 요청입니다.", HttpStatus.FORBIDDEN),
  ;

  private final String message;
  private final HttpStatus status;
}
