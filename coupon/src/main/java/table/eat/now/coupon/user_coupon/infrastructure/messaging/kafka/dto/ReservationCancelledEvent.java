package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto;

public record ReservationCancelledEvent(
    EventType eventType,
    String reservationUuid,
    ReservationCancelledPayload payload

) implements ReservationEvent {

  public static ReservationCancelledEvent from(ReservationCancelledPayload payload){
    return new ReservationCancelledEvent(EventType.RESERVATION_CANCELLED, payload.reservationUuid(), payload);
  }

}
