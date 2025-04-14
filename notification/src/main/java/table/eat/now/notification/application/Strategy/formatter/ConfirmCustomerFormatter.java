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
public class ConfirmCustomerFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.CONFIRM_CUSTOMER;
  }

  //사용자측 예약 확인 알림
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "예약이 확정 되었습니다.",
        String.format("%s에 예약이 확정 되었습니다. %s에 방문 해주세요!",
            params.get("restaurantName"),params.get("reservationTime"))
    );
  }
}

