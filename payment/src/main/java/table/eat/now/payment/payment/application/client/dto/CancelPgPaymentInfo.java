package table.eat.now.payment.payment.application.client.dto;

import java.time.LocalDateTime;
import table.eat.now.payment.payment.domain.entity.CancelPayment;

public record CancelPgPaymentInfo(
    String paymentKey,
    String cancelReason,
    LocalDateTime cancelledAt
) {

  public CancelPayment toCancel(){
    return new CancelPayment(paymentKey,cancelReason,cancelledAt);
  }
}
