package table.eat.now.notification.application.event.produce;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.notification.application.event.EventType;
import table.eat.now.notification.application.event.NotificationEvent;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 18.
 */
public record NotificationSendEvent(
    EventType eventType,
    NotificationSendPayload payload,
    CurrentUserInfoDto userInfoDto
) implements NotificationEvent {

}
