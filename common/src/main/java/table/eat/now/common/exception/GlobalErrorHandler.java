package table.eat.now.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static table.eat.now.common.constant.ErrorMessageConstant.TYPE_MISMATCH_FORMAT;
import static table.eat.now.common.constant.ErrorMessageConstant.UNSPECIFIED_TYPE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import table.eat.now.common.exception.type.ApiErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e, HttpServletRequest request) {

    List<ErrorResponse.ErrorField> errorFields = e.getConstraintViolations().stream()
        .map(violation -> new ErrorResponse.ErrorField(
            violation.getInvalidValue(),
            violation.getMessage()))
        .toList();

    log(e, request, HttpStatus.BAD_REQUEST);
    return ResponseEntity
        .badRequest()
        .body(ErrorResponse.of(ApiErrorCode.INVALID_REQUEST.getMessage(), errorFields));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e,
      HttpServletRequest request) {

    List<ErrorResponse.ErrorField> errorFields = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> new ErrorResponse.ErrorField(
            fieldError.getField(),
            fieldError.getDefaultMessage()))
        .toList();

    log(e, request, HttpStatus.BAD_REQUEST);
    return ResponseEntity
        .badRequest()
        .body(ErrorResponse.of(ApiErrorCode.INVALID_REQUEST.getMessage(), errorFields));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e,
      HttpServletRequest request) {

    List<ErrorResponse.ErrorField> errorFields = List.of(
        new ErrorResponse.ErrorField(
            e.getName(), String.format(TYPE_MISMATCH_FORMAT,
            e.getValue(),
            e.getRequiredType() != null ?
                e.getRequiredType().getSimpleName() : UNSPECIFIED_TYPE)));

    log(e, request, HttpStatus.BAD_REQUEST);
    return ResponseEntity
        .badRequest()
        .body(ErrorResponse.of(ApiErrorCode.TYPE_MISMATCH.getMessage(), errorFields));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
      HandlerMethodValidationException e,
      HttpServletRequest request) {

    List<ErrorResponse.ErrorField> errorFields = e.getValueResults()
        .stream()
        .map(result -> new ErrorResponse.ErrorField(
            result.getMethodParameter().getParameterName(),
            result.getResolvableErrors().get(0).getDefaultMessage()))
        .toList();

    log(e, request, HttpStatus.BAD_REQUEST);
    return ResponseEntity
        .badRequest()
        .body(ErrorResponse.of(ApiErrorCode.INVALID_REQUEST.getMessage(), errorFields));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleException(CustomException e,
      HttpServletRequest request) {

    log(e, request, e.getStatus());
    return ResponseEntity.status(e.getStatus())
        .body(ErrorResponse.from(e));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e,
      HttpServletRequest request) {

    HttpStatus status = INTERNAL_SERVER_ERROR;
    log(e, request, status);
    return ResponseEntity.status(status).body(ErrorResponse.from(e));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorResponse> handleThrowable(Throwable e,
      HttpServletRequest request) {

    HttpStatus status = INTERNAL_SERVER_ERROR;
    log(e, request, status);
    return ResponseEntity.status(status).body(ErrorResponse.from(e));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e,
      HttpServletRequest request){

    HttpStatus status = BAD_REQUEST;
    log(e, request, status);
    return ResponseEntity.status(status).body(ErrorResponse.from(e));
  }

  private static void log(Throwable e, HttpServletRequest request, HttpStatus status) {

    log.error("{}:{}:{}:{}", request.getRequestURI(), status.value(), e.getClass().getSimpleName(),
        e.getMessage());
  }
}
