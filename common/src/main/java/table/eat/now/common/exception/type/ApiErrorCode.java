package table.eat.now.common.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiErrorCode implements ErrorCode {
  INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("접근이 금지 되었습니다.", HttpStatus.FORBIDDEN),
  NOT_FOUND("요청한 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR("서버 에러 입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  NOT_IMPLEMENTED("요청한 URI 페이지는 없습니다.",
      HttpStatus.NOT_IMPLEMENTED),
  BAD_GATEWAY("서버 간 통신이 올바르지 않습니다.", HttpStatus.BAD_GATEWAY);

  private final String message;
  private final HttpStatus status;
}
