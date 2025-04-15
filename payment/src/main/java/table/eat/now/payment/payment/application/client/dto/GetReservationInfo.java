package table.eat.now.payment.payment.application.client.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record GetReservationInfo(
    String reservationUuid,
    String restaurantUuid,
    Long customerId,
    String reservationName,
    String status,
    BigDecimal totalAmount
) {

}