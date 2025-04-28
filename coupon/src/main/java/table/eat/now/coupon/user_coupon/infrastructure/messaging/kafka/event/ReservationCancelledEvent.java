package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.event;

public record ReservationCancelledEvent(
    String eventType,
    String reservationUuid,
    ReservationCancelledPayload payload

) implements ReservationEvent {

}
