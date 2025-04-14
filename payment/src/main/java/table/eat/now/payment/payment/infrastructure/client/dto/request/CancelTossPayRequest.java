package table.eat.now.payment.payment.infrastructure.client.dto.request;

import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;

public record CancelTossPayRequest(
    String cancelReason
) {
  public static CancelTossPayRequest from(CancelPaymentCommand command) {
    return new CancelTossPayRequest(command.cancelReason());
  }
}
