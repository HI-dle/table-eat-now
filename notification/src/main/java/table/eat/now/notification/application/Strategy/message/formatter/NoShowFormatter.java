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
public class NoShowFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.NO_SHOW;
  }

  //노쇼 발생시 알림(사용자 측에 발송)
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "노쇼 하셨습니다.",
        String.format("%s에 %s으로 예약 후 방문하지 않았습니다.",
            params.get("restaurantName"), params.get("reservationTime"))
    );
  }
}

