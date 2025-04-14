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
public class Reminder9AmFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.REMINDER_9AM;
  }

  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "오늘 예약이 있습니다.",
        String.format("오늘 %s에 %s으로 예약이 있어요. 확인해주세요!",
            params.get("restaurantName"),params.get("reservationTime"))
    );
  }
}

