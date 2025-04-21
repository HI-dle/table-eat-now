package table.eat.now.payment.payment.application.client.dto;

import java.math.BigDecimal;

public record CancelPgPaymentCommand(
    String paymentKey,
    String cancelReason,
    BigDecimal cancelAmount
) {
  public static CancelPgPaymentCommand of(
      String paymentKey,
      String cancelReason,
      BigDecimal cancelAmount
  ) {
    return new CancelPgPaymentCommand(
        paymentKey,
        cancelReason,
        cancelAmount
    );
  }
}