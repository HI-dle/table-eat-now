/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 20.
 */
package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listener.dto;

public enum EventType {
  RESERVATION_PAYMENT_SUCCEED,

  RESERVATION_PAYMENT_FAILED,
  RESERVATION_PAYMENT_CANCEL_SUCCEED
  ;
}
