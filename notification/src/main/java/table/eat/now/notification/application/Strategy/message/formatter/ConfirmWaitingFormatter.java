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
public class ConfirmWaitingFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.CONFIRM_WAITING;
  }

  //대기 신청 완료 알림 (데이터 받아올 때 대기인원 수도 넣을 수 있습니다.)
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "대기 신청이 완료되었습니다",
        String.format("%s님, %s에 대기 신청이 완료되었습니다. 호출 시 바로 입장해주세요.",
            params.get("customerName"), params.get("restaurantName"))
    );
  }
}

