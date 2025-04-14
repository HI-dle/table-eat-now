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
public class Reminder1HrFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.REMINDER_1HR;
  }

  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "예약 1시간 전 알림입니다!",
        String.format("%s에 %s 예약시간 1시간 전 입니다. 늦지 않게 도착해주세요!",
            params.get("restaurantName"), params.get("reservationTime"))
    );
  }
}

