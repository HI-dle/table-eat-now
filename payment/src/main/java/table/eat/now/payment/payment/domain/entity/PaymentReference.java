package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
public class PaymentReference {

  @Column(name = "restaurant_uuid", nullable = false)
  private String restaurantId;

  @Column(name = "reservation_uuid", nullable = false)
  private String reservationId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "customer_key", nullable = false)
  private String customerKey;

  @Column(name = "reservation_name", nullable = false)
  private String reservationName;

  public static PaymentReference create(
      String restaurantId, String reservationId, Long customerId, String reservationName) {

    validateNull(restaurantId, reservationId, customerId, reservationName);
    return new PaymentReference(restaurantId, reservationId, customerId, reservationName);
  }

  private static void validateNull(
      String restaurantId, String reservationId, Long customerId, String reservationName) {

    if(restaurantId == null || reservationId == null || customerId == null || reservationName == null) {
      throw new IllegalArgumentException("null이 될 수 없습니다.");
    }
  }

  private PaymentReference(
      String restaurantId, String reservationId, Long customerId, String reservationName) {
    this.restaurantId = restaurantId;
    this.reservationId = reservationId;
    this.customerId = customerId;
    this.customerKey = UUID.randomUUID().toString();
    this.reservationName = reservationName;
  }

  protected PaymentReference() {
  }
}
