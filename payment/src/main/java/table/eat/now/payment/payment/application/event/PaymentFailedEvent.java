package table.eat.now.payment.payment.application.event;

import static table.eat.now.payment.payment.application.event.EventType.RESERVATION_PAYMENT_FAILED;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentFailedEvent(
    EventType eventType,
    String paymentUuid,
    PaymentFailedPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static PaymentFailedEvent of(
      PaymentFailedPayload payload, CurrentUserInfoDto userInfo) {
    return new PaymentFailedEvent(
        RESERVATION_PAYMENT_FAILED, payload.paymentUuid(), payload, userInfo);
  }
}
