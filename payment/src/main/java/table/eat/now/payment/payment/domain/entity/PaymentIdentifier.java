package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.Getter;

@Getter
@Embeddable
public class PaymentIdentifier {

		@Column(nullable = false, unique = true)
		private UUID paymentUuid;

		@Column(nullable = false, unique = true)
		private UUID idempotencyKey;

		protected PaymentIdentifier() {
		}
}
