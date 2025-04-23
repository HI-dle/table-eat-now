/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.event.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail.PaymentType;

@Builder
public record ConfirmReservationAfterCommitEvent(
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

  public static ConfirmReservationAfterCommitEvent from(
      Reservation reservation
  ) {

    List<String> couponUuids = reservation.getPaymentDetails()
        .getValues()
        .stream()
        .filter(detail -> detail.getType() == PaymentType.PROMOTION_COUPON)
        .map(couponDetail -> couponDetail.getDetailReferenceId())
        .toList();

    return ConfirmReservationAfterCommitEvent.builder()
        .reservationUuid(reservation.getReservationUuid())
        .reservationName(reservation.getName())
        .restaurantUuid(reservation.getRestaurantUuid())
        .restaurantName(reservation.getRestaurantDetails().getName())
        .restaurantTimeSlotUuid(reservation.getRestaurantTimeSlotUuid())
        .reservationDateTime(reservation.getRestaurantTimeSlotDetails().reservationDateTime())
        .couponUuids(couponUuids)
        .reserverId(reservation.getReserverId())
        .reserverName(reservation.getGuestInfo().getReserverName())
        .guestCount(reservation.getGuestInfo().getGuestCount())
        .processedAt(java.time.LocalDateTime.now())
        .build();
  }
}
