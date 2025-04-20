package table.eat.now.notification.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

  INVALID_NOTIFICATION_UUID(
      "존재하지 않는 알림입니다.", HttpStatus.NOT_FOUND),
  INVALID_NOTIFICATION_TYPE(
      "존재하지 않는 알림 타입 입니다.", HttpStatus.NOT_FOUND),
  UNSUPPORTED_NOTIFICATION_METHOD(
      "존재하지 않는 알림 전송 유형 입니다.", HttpStatus.NOT_FOUND),
  NOTIFICATION_SEND_FAIL(
      "알림 전송 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String message;
  private final HttpStatus status;
}
