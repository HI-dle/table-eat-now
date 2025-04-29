package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.event;

public interface ReservationEvent extends CommonEvent {

  String reservationUuid();
}
