/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.event.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.reservation.reservation.application.event.event.ConfirmReservationAfterCommitEvent;

@Builder
public record ReservationConfirmedPayload(
    // 예약
    String reservationUuid,
    String reservationName,

    // 식당, 타임슬롯
    String restaurantUuid,
    String restaurantName,
    String restaurantTimeSlotUuid,
    LocalDateTime reservationDateTime,

    // 쿠폰
    List<String> couponUuids,

    // 예약자 정보
    Long reserverId,
    String reserverName,
    Integer guestCount,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime processedAt
) {
    public static ReservationConfirmedPayload from(ConfirmReservationAfterCommitEvent event){
        return ReservationConfirmedPayload.builder()
            .reservationUuid(event.reservationUuid())
            .reservationName(event.reservationName())
            .restaurantUuid(event.restaurantUuid())
            .restaurantName(event.restaurantName())
            .restaurantTimeSlotUuid(event.restaurantTimeSlotUuid())
            .reservationDateTime(event.reservationDateTime())
            .couponUuids(event.couponUuids())
            .reserverId(event.reserverId())
            .reserverName(event.reserverName())
            .guestCount(event.guestCount())
            .processedAt(java.time.LocalDateTime.now())
            .build();
    }
}
