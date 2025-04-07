package table.eat.now.common.exception.type;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  int getCode();

  String getMessage();

  HttpStatus getStatus();
}
