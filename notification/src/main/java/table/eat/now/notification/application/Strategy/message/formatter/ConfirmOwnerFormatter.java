package table.eat.now.notification.application.Strategy.message.formatter;

import java.util.Map;
import org.springframework.stereotype.Component;
import table.eat.now.notification.application.Strategy.NotificationFormatterStrategy;
import table.eat.now.notification.application.Strategy.NotificationTemplate;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 14.
 */
@Component
public class ConfirmOwnerFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.CONFIRM_OWNER;
  }

  //레스토랑 측 예약 확인 요청
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "예약 확인 요청이 도착했습니다.",
        String.format("%s님이 %s에 예약을 신청했습니다. 확인해주세요.",
            params.get("customerName"), params.get("reservationTime"))
    );
  }
}

