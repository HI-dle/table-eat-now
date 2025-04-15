package table.eat.now.payment.payment.application.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.ConfirmPayment;

public record ConfirmPgPaymentInfo(
    String paymentKey,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    LocalDateTime approvedAt
) {

  public ConfirmPayment toConfirm() {
    return new ConfirmPayment(paymentKey, discountAmount, totalAmount, approvedAt);
  }
}
