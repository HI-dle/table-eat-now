package table.eat.now.payment.payment.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;

public record ConfirmPaymentRequest(
    @NotNull String paymentKey,
    @NotNull @Positive BigDecimal totalAmount
) {
  public ConfirmPaymentCommand toCommand(String reservationId){
    return new ConfirmPaymentCommand(reservationId, paymentKey, totalAmount);
  }
}
