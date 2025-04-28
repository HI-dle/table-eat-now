package table.eat.now.notification.application.event.produce;

import table.eat.now.notification.application.event.EventType;
import table.eat.now.notification.application.event.NotificationEvent;
import table.eat.now.notification.domain.entity.Notification;
import table.eat.now.notification.domain.entity.NotificationMethod;
import table.eat.now.notification.domain.entity.NotificationStatus;
import table.eat.now.notification.domain.entity.NotificationType;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
public record NotificationPromotionEvent(
    EventType eventType,
    NotificationPromotionPayload payload
)implements NotificationEvent {

  public Notification toEntity() {
    return Notification.of(
        payload().userId(),
        NotificationType.PROMOTION_PARTICIPATE,
        payload().userId().toString(),
        null,
        null,
        NotificationStatus.SENT,
        NotificationMethod.SLACK,
        null
    );
  }
}
