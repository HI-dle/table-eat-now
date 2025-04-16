package table.eat.now.payment.payment.application.event;

import static table.eat.now.payment.payment.application.event.EventType.CANCEL_SUCCEED;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentCanceledEvent(
    EventType eventType,
    String paymentUuid,
    PaymentCanceledPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static PaymentCanceledEvent of(
      PaymentCanceledPayload payload, CurrentUserInfoDto userInfo) {
    return new PaymentCanceledEvent(
        CANCEL_SUCCEED, payload.paymentUuid(), payload, userInfo);
  }

}
