package table.eat.now.notification.application.strategy.send;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.notification.application.exception.NotificationErrorCode;
import table.eat.now.notification.domain.entity.NotificationMethod;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Component
public class NotificationSenderStrategySelector {

  private final Map<NotificationMethod, NotificationSenderStrategy> strategyMap;

  public NotificationSenderStrategySelector(List<NotificationSenderStrategy> strategies) {
    this.strategyMap = strategies.stream()
        .collect(Collectors.toMap(NotificationSenderStrategy::getMethod, method -> method));
  }

  public NotificationSenderStrategy select(NotificationMethod method) {
    NotificationSenderStrategy strategy = strategyMap.get(method);
    if (strategy == null) {
      throw new CustomException(NotificationErrorCode.UNSUPPORTED_NOTIFICATION_METHOD);
    }
    return strategy;
  }
}
