package table.eat.now.payment.payment.infrastructure.kafka.event;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentFailedEvent(
    EventType eventType,
    String paymentUuid,
    PaymentFailedPayload payload,
    CurrentUserInfoDto userInfo
) {
}
