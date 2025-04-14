package table.eat.now.payment.payment.application.dto.request;

public record CancelPaymentCommand(
    String paymentKey,
    String cancelReason
) {
  public static CancelPaymentCommand of(String paymentKey, String cancelReason) {
    return new CancelPaymentCommand(paymentKey, cancelReason);
  }
}