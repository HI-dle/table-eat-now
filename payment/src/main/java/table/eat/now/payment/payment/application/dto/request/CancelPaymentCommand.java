package table.eat.now.payment.payment.application.dto.request;

public record CancelPaymentCommand(
    String reservationUuid,
    String idempotencyKey,
    String cancelReason
) {

}
