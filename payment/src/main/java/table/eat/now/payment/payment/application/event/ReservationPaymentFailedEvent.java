package table.eat.now.payment.payment.application.event;

import static table.eat.now.payment.payment.application.event.EventType.RESERVATION_PAYMENT_FAILED;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record ReservationPaymentFailedEvent(
    EventType eventType,
    String paymentUuid,
    ReservationPaymentFailedPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static ReservationPaymentFailedEvent of(
      ReservationPaymentFailedPayload payload, CurrentUserInfoDto userInfo) {
    return new ReservationPaymentFailedEvent(
        RESERVATION_PAYMENT_FAILED, payload.paymentUuid(), payload, userInfo);
  }
}
