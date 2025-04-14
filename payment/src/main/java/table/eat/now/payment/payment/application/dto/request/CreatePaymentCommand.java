package table.eat.now.payment.payment.application.dto.request;

import java.math.BigDecimal;
import lombok.Builder;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.entity.PaymentAmount;
import table.eat.now.payment.payment.domain.entity.PaymentReference;

@Builder
public record CreatePaymentCommand(
    String reservationUuid,
    String restaurantUuid,
    Long customerId,
    String reservationName,
    BigDecimal originalAmount) {

  public PaymentReference toReference() {
    return PaymentReference.create(
        restaurantUuid, reservationUuid, customerId, reservationName);
  }

  public Payment toEntity() {
    return Payment.create(this.toReference(), PaymentAmount.create(originalAmount));
  }
}



