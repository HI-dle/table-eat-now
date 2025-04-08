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
  ;

  private final String message;
  private final HttpStatus status;
}
