package table.eat.now.waiting.waiting.infrastructure.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WaitingInfraErrorCode implements ErrorCode {

  INVALID_DAILY_WAITING_UUID(
      "유효하지 않은 일간 대기 정보 아이디입니다.", HttpStatus.NOT_FOUND);

  private final String message;
  private final HttpStatus status;
}
