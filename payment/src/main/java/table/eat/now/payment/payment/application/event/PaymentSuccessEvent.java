package table.eat.now.payment.payment.application.event;


import static table.eat.now.payment.payment.application.event.EventType.RESERVATION_PAYMENT_SUCCEED;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentSuccessEvent(
    EventType eventType,
    String paymentUuid,
    PaymentSuccessPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static PaymentSuccessEvent of(
      PaymentSuccessPayload payload, CurrentUserInfoDto userInfo) {

    return new PaymentSuccessEvent(
        RESERVATION_PAYMENT_SUCCEED, payload.paymentUuid(), payload, userInfo);
  }
}
