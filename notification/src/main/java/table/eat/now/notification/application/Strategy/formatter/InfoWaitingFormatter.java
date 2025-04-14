package table.eat.now.notification.application.Strategy.formatter;

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
public class InfoWaitingFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.INFO_WAITING;
  }

  //대기 끝 입장 알림
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "입장 안내 알림입니다.",
        String.format("%s님, 입장 가능하니 %s으로 입장 해주세요.",
            params.get("customerName"), params.get("restaurantName"))
    );
  }
}
