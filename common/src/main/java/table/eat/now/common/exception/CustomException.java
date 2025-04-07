package table.eat.now.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

  private final int code;
  private final HttpStatus status;

  public CustomException(ErrorCode errorCode) {

    super(errorCode.getMessage());
    this.status = errorCode.getStatus();
    this.code = errorCode.getCode();
  }

  public CustomException(HttpStatus httpStatus, int code, String message) {

    super(message);
    this.status = httpStatus;
    this.code = code;
  }

  public static CustomException from(ErrorCode errorCode) {
    return new CustomException(errorCode);
  }
}
