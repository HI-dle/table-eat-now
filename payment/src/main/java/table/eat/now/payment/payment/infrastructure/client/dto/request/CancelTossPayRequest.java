package table.eat.now.payment.payment.infrastructure.client.dto.request;

import java.math.BigDecimal;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentCommand;

public record CancelTossPayRequest(
    String cancelReason,
    BigDecimal cancelAmount
) {
  public static CancelTossPayRequest from(CancelPgPaymentCommand command) {
    return new CancelTossPayRequest(command.cancelReason(), command.cancelAmount());
  }
}
