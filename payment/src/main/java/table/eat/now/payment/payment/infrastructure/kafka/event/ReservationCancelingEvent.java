package table.eat.now.payment.payment.infrastructure.kafka.event;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record ReservationCancelingEvent(
    EventType eventType,
    String reservationUuid,
    ReservationCancelingPayload payload,
    CurrentUserInfoDto userInfo
) {


}
