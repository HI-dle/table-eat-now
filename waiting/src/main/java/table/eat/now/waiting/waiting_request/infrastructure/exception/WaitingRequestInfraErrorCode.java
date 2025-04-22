package table.eat.now.waiting.waiting_request.infrastructure.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WaitingRequestInfraErrorCode implements ErrorCode {
  FAILED_ENQUEUE(
      "대기열 등록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE),
  INVALID_WAITING_REQUEST_UUID(
      "유효하지 않은 대기 요청 아이디입니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus status;
}
