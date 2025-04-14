package table.eat.now.payment.payment.presentation.dto.request;

import java.math.BigDecimal;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;

public record ConfirmPaymentRequest(
    String paymentKey,
    BigDecimal totalAmount
) {
  public ConfirmPaymentCommand toCommand(String reservationId){
    return new ConfirmPaymentCommand(reservationId, paymentKey, totalAmount);
  }
}
