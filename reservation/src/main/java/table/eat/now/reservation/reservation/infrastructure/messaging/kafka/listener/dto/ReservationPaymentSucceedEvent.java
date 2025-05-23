package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listener.dto;


import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.reservation.reservation.application.service.dto.request.ConfirmReservationCommand;

public record ReservationPaymentSucceedEvent(
    EventType eventType,
    String paymentUuid,
    ReservationPaymentSucceedPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public ConfirmReservationCommand toCommand() {
    return ConfirmReservationCommand.builder()
        .idempotencyKey(payload.idempotencyKey())
        .build();
  }
}
