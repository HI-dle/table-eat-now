package table.eat.now.reservation.reservation.infrastructure.messaging.kafka.listner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReservationPaymentSucceedPayload(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    BigDecimal originalAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime approvedAt
) {

}
