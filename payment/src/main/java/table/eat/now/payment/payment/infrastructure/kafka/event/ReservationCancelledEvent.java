package table.eat.now.payment.payment.infrastructure.kafka.event;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record ReservationCancelledEvent(
    EventType eventType,
    String reservationUuid,
    ReservationCancelledPayload payload,
    CurrentUserInfoDto userInfo
) {


}
