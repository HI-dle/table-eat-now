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
      "대기열 등록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE),;

  private final String message;
  private final HttpStatus status;
}
