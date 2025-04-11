package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
public class PaymentReference {

  @Column(name = "restaurant_uuid", nullable = false, columnDefinition = "VARCHAR(100)")
  private UUID restaurantId;

  @Column(name = "reservation_uuid", nullable = false, columnDefinition = "VARCHAR(100)")
  private UUID reservationId;

  @Column(name = "customer_id", nullable = false, columnDefinition = "VARCHAR(100)")
  private Long customerId;

  protected PaymentReference() {
  }
}
