package table.eat.now.payment.payment.application.dto.request;

import java.math.BigDecimal;

public record ConfirmPaymentCommand(
    String reservationId,
    String paymentKey,
    BigDecimal totalAmount
) {
}
