package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;

public record GetCheckoutDetailResponse(
    String idempotencyKey,
    String customerKey,
    BigDecimal amount,
    String orderName) {

}
