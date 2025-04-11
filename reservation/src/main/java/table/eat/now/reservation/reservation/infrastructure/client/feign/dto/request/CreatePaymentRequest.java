/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request;

import java.math.BigDecimal;
import table.eat.now.reservation.reservation.application.client.dto.request.CreatePaymentCommand;

public record CreatePaymentRequest(
    String reservationUuid,
    String restaurantUuid,
    Long customerId,
    String reservationName,
    BigDecimal originalAmount
) {
  public static CreatePaymentRequest from(CreatePaymentCommand command) {
    return new CreatePaymentRequest(
        command.reservationUuid(),
        command.restaurantUuid(),
        command.customerId(),
        command.reservationName(),
        command.originalAmount()
    );
  }
}

