package table.eat.now.notification.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import table.eat.now.notification.application.dto.request.NotificationSearchCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record NotificationSearchCondition(Long userId,
                                          @Pattern(regexp = "CONFIRM_OWNER|CONFIRM_CUSTOMER|"
                                              + "REMINDER_9AM|REMINDER_1HR|COMPLETION|NO_SHOW",
                                              message = "유효하지 않은 알림 유형입니다.")
                                          String notificationType,
                                          String message,
                                          @Pattern(regexp = "PENDING|SENT|FAILED",
                                              message = "유효하지 않은 알림 상태입니다.")
                                          String status,
                                          @Pattern(regexp = "SLACK|EMAIL|MESSAGE",
                                              message = "유효하지 않은 알림 방식입니다.")
                                          String notificationMethod,
                                          Boolean isAsc,
                                          String sortBy,
                                          int page,
                                          int size) {
  public NotificationSearchCommand toApplication() {
    return new NotificationSearchCommand(
        userId,
        notificationType,
        message,
        status,
        notificationMethod,
        isAsc,
        sortBy,
        page,
        size
    );
  }

}
