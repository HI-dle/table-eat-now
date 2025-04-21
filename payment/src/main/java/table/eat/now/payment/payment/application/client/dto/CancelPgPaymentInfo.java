package table.eat.now.payment.payment.application.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.CancelPayment;

public record CancelPgPaymentInfo(
    String paymentKey,
    String cancelReason,
    BigDecimal cancelAmount,
    BigDecimal balanceAmount,
    LocalDateTime cancelledAt
) {

  public CancelPayment toCancel() {
    return new CancelPayment(
        paymentKey,
        cancelReason,
        cancelAmount,
        balanceAmount,
        cancelledAt
    );
  }
}
