package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto;

public interface ReservationEvent extends CommonEvent {

  String reservationUuid();
}
