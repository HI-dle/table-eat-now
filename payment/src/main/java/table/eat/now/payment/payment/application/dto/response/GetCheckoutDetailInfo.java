package table.eat.now.payment.payment.application.dto.response;

import java.math.BigDecimal;
import table.eat.now.payment.payment.domain.entity.Payment;

public record GetCheckoutDetailInfo(
    String idempotencyKey,
    String customerKey,
    BigDecimal originalAmount,
    String reservationName,
    String reservationId
) {

  public static GetCheckoutDetailInfo from(Payment payment) {

    return new GetCheckoutDetailInfo(
        payment.getIdentifier().getIdempotencyKey(),
        payment.getReference().getCustomerKey(),
        payment.getAmount().getOriginalAmount(),
        payment.getReference().getReservationName(),
        payment.getReference().getReservationId()
    );
  }
}
