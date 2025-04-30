package table.eat.now.notification.application.strategy.message.formatter;

import java.util.Map;
import org.springframework.stereotype.Component;
import table.eat.now.notification.application.strategy.NotificationFormatterStrategy;
import table.eat.now.notification.application.strategy.NotificationTemplate;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 14.
 */
@Component
public class PromotionParticipateFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.PROMOTION_PARTICIPATE;
  }

  //프로모션 신청 성공
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "프로모션에 신청 되었습니다!",
        String.format("%s님, 신청해주셔서 감사합니다.",
            params.get("customerName"))
    );
  }
}

