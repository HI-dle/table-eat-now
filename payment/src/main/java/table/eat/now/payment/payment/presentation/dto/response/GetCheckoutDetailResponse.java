package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record GetCheckoutDetailResponse(
		UUID idempotencyKey, Long customerId, BigDecimal amount, String orderName) {
}
