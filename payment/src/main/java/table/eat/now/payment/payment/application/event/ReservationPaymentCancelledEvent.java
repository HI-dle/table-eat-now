package table.eat.now.payment.payment.application.event;

import static table.eat.now.payment.payment.application.event.EventType.RESERVATION_PAYMENT_CANCEL_SUCCEED;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record ReservationPaymentCancelledEvent(
    EventType eventType,
    String paymentUuid,
    ReservationPaymentCancelledPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static ReservationPaymentCancelledEvent of(
      ReservationPaymentCancelledPayload payload, CurrentUserInfoDto userInfo) {
    return new ReservationPaymentCancelledEvent(
        RESERVATION_PAYMENT_CANCEL_SUCCEED, payload.paymentUuid(), payload, userInfo);
  }
}
