package table.eat.now.coupon.user_coupon.infrastructure.messaging.kafka.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ReservationCancelledPayload(
    String reservationUuid,

    // 결제
    String paymentIdempotencyKey,
    BigDecimal cancelAmount, // 결제 취소 금액
    String cancelReason, // 최대 200자

    // 식당
    String restaurantUuid,
    Integer guestCount,

    // 쿠폰
    List<String> couponUuids,

    // 유저
    Long reserverId,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime processedAt
) {

}
