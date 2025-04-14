package table.eat.now.payment.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.ConfirmPayment;

public record ConfirmPgPaymentInfo(
    String paymentKey,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    LocalDateTime approvedAt
) {
  //커맨드 객체의 네이밍 고민..
  public ConfirmPayment toConfirm() {
    return new ConfirmPayment(paymentKey, discountAmount, totalAmount, approvedAt);
  }
}
