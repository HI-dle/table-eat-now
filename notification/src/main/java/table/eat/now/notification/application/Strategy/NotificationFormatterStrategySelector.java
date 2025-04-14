package table.eat.now.notification.application.Strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.eclipse.sisu.PostConstruct;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.notification.application.exception.NotificationErrorCode;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 14.
 */
@Component
public class NotificationFormatterStrategySelector {

  private final List<NotificationFormatterStrategy> strategies;
  private final Map<NotificationType, NotificationFormatterStrategy> strategyMap =
      new EnumMap<>(NotificationType.class);

  public NotificationFormatterStrategySelector(List<NotificationFormatterStrategy> strategies) {
    this.strategies = strategies;
  }

  @PostConstruct
  public void init() {
    for (NotificationFormatterStrategy strategy : strategies) {
      strategyMap.put(strategy.getType(), strategy);
    }
  }

  public NotificationFormatterStrategy select(NotificationType type) {
    NotificationFormatterStrategy strategy = strategyMap.get(type);
    if (strategy == null) {
      throw CustomException.from(NotificationErrorCode.INVALID_NOTIFICATION_TYPE);
    }
    return strategy;
  }
}
