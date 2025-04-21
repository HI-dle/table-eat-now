package table.eat.now.payment.payment.application.event;


import static table.eat.now.payment.payment.application.event.EventType.RESERVATION_PAYMENT_SUCCEED;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record ReservationPaymentSucceedEvent(
    EventType eventType,
    String paymentUuid,
    ReservationPaymentSucceedPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static ReservationPaymentSucceedEvent of(
      ReservationPaymentSucceedPayload payload, CurrentUserInfoDto userInfo) {

    return new ReservationPaymentSucceedEvent(
        RESERVATION_PAYMENT_SUCCEED, payload.paymentUuid(), payload, userInfo);
  }
}
