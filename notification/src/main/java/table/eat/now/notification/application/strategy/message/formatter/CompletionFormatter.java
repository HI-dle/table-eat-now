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
public class CompletionFormatter implements NotificationFormatterStrategy {

  @Override
  public NotificationType getType() {
    return NotificationType.COMPLETION;
  }

  //방문 확인 알림
  @Override
  public NotificationTemplate format(Map<String, String> params) {
    return new NotificationTemplate(
        "방문 감사합니다!",
        String.format("%s님, 방문해주셔서 감사합니다.",
            params.get("customerName"))
    );
  }
}

