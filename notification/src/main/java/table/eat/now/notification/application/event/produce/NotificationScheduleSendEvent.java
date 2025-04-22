package table.eat.now.notification.application.event.produce;

import lombok.Builder;
import table.eat.now.notification.application.event.EventType;
import table.eat.now.notification.application.event.NotificationEvent;
import table.eat.now.notification.domain.entity.Notification;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 18.
 */
@Builder
public record NotificationScheduleSendEvent(
    EventType eventType,
    NotificationScheduleSendPayload payload
) implements NotificationEvent {

  public static NotificationScheduleSendEvent from(Notification notification) {
    return NotificationScheduleSendEvent.builder()
        .eventType(EventType.Schedule_SEND)
        .payload(NotificationScheduleSendPayload.from(notification))
        .build();
  }

}
