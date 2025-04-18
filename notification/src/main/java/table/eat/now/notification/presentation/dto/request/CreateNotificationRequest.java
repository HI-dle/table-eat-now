package table.eat.now.notification.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import table.eat.now.notification.application.dto.request.CreateNotificationCommand;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public record CreateNotificationRequest(@NotNull
                                        Long userId,
                                        @NotBlank(message = "알림 유형은 필수입니다.")
                                        @Pattern(regexp = "CONFIRM_OWNER|CONFIRM_CUSTOMER|"
                                            + "REMINDER_9AM|REMINDER_1HR|COMPLETION|NO_SHOW",
                                            message = "유효하지 않은 알림 유형입니다.")
                                        String notificationType,
                                        String customerName,
                                        LocalDateTime reservationTime,
                                        String restaurantName,
                                        @NotBlank(message = "알림 상태는 필수입니다.")
                                        @Pattern(regexp = "PENDING|SENT|FAILED",
                                            message = "유효하지 않은 알림 상태입니다.")
                                        String status,
                                        @NotBlank(message = "알림 방식은 필수입니다.")
                                        @Pattern(regexp = "SLACK|EMAIL|MESSAGE",
                                            message = "유효하지 않은 알림 방식입니다.")
                                        String notificationMethod,
                                        @Future(message = "예약 발송 시간은 현재 이후 시간이어야 합니다.")
                                        LocalDateTime scheduledTime) {
  public CreateNotificationCommand toApplication() {
    return new CreateNotificationCommand(
        userId,
        notificationType,
        customerName,
        reservationTime,
        restaurantName,
        status,
        notificationMethod,
        scheduledTime
    );
  }
}
