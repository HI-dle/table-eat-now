package table.eat.now.payment.payment.application.dto.response;

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