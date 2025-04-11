/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CreatePaymentCommand(
    String reservationUuid,
    String restaurantUuid,
    Long customerId,
    String reservationName,
    BigDecimal originalAmount
) {

  public static CreatePaymentCommand from(
      String reservationUuid,
      String restaurantUuid,
      Long customerId,
      String reservationName,
      BigDecimal originalAmount
  ) {
    return CreatePaymentCommand.builder()
        .reservationUuid(reservationUuid)
        .restaurantUuid(restaurantUuid)
        .customerId(customerId)
        .reservationName(reservationName)
        .originalAmount(originalAmount)
        .build();
  }
}
