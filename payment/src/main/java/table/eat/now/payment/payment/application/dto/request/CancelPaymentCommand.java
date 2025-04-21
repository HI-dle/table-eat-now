package table.eat.now.payment.payment.application.dto.request;

import java.math.BigDecimal;

public record CancelPaymentCommand(
    String reservationUuid,
    String idempotencyKey,
    BigDecimal cancelAmount,
    String cancelReason
) {
}
