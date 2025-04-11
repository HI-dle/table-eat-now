package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
public class PaymentIdentifier {

  @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
  private UUID paymentUuid;

  @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
  private UUID idempotencyKey;

  protected PaymentIdentifier() {
  }
}
