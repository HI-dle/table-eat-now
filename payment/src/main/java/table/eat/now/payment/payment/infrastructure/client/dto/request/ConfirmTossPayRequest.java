package table.eat.now.payment.payment.infrastructure.client.dto.request;

import java.math.BigDecimal;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;

public record ConfirmTossPayRequest(
    String orderId,
    String paymentKey,
    BigDecimal amount
) {

  public static ConfirmTossPayRequest from(ConfirmPaymentCommand command) {
    return new ConfirmTossPayRequest(
        command.reservationId(),
        command.paymentKey(),
        command.totalAmount());
  }
}
