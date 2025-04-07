package table.eat.now.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record ErrorResponse(
    String message,
    @JsonInclude(JsonInclude.Include.NON_NULL) List<ErrorField> errors
) {

  public static ErrorResponse from(Throwable ex) {
    return new ErrorResponse(ex.getMessage(), null);
  }

  public static ErrorResponse from(Exception ex) {
    return new ErrorResponse(ex.getMessage(), null);
  }

  public static ErrorResponse of(String message, List<ErrorField> errors) {
    return new ErrorResponse(message, errors);
  }

  public record ErrorField(Object value, String message) {

  }
}