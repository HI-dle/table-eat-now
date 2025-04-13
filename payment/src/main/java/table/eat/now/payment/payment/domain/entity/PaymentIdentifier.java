package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
public class PaymentIdentifier {

  @Column(nullable = false, unique = true)
  private String paymentUuid;

  @Column(nullable = false, unique = true)
  private String idempotencyKey;

  public static PaymentIdentifier create() {
    return new PaymentIdentifier(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString()
    );
  }

  private PaymentIdentifier(String paymentUuid, String idempotencyKey) {
    this.paymentUuid = paymentUuid;
    this.idempotencyKey = idempotencyKey;
  }

  protected PaymentIdentifier() {
  }
}
