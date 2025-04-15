package table.eat.now.payment.payment.infrastructure.kafka.event;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentSuccessEvent(
    EventType eventType,
    String paymentUuid,
    PaymentSuccessPayload payload,
    CurrentUserInfoDto userInfo
) {

}
