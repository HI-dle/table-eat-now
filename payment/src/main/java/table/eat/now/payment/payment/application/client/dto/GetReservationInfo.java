package table.eat.now.payment.payment.application.client.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record GetReservationInfo(
    String status,
    BigDecimal totalAmount
) {

}