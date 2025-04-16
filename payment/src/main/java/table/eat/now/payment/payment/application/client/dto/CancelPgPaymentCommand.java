package table.eat.now.payment.payment.application.client.dto;

public record CancelPgPaymentCommand(
    String paymentKey,
    String cancelReason
) {
  public static CancelPgPaymentCommand of(String paymentKey, String cancelReason) {
    return new CancelPgPaymentCommand(paymentKey, cancelReason);
  }
}