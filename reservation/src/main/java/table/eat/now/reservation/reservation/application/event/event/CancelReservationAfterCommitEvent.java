/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.event.event;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail.PaymentType;

@Builder
public record CancelReservationAfterCommitEvent(
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
    Long reserverId
) {

  public static CancelReservationAfterCommitEvent from(
      Reservation reservation
  ) {
    ReservationPaymentDetail paymentDetail = reservation.getPaymentDetails().getValues()
        .stream()
        .filter(detail -> detail.getType() == PaymentType.PAYMENT)
        .findFirst().get();

    List<String> couponUuids = reservation.getPaymentDetails()
        .getValues()
        .stream()
        .filter(detail -> detail.getType() == PaymentType.PROMOTION_COUPON)
        .map(couponDetail -> couponDetail.getDetailReferenceId())
        .toList();

    return CancelReservationAfterCommitEvent.builder()
        .reservationUuid(reservation.getReservationUuid())
        .paymentIdempotencyKey(paymentDetail.getReservationPaymentDetailUuid())
        .cancelAmount(paymentDetail.getAmount())
        .cancelReason(reservation.getCancelReason())
        .restaurantUuid(reservation.getRestaurantUuid())
        .guestCount(reservation.getGuestInfo().getGuestCount())
        .couponUuids(couponUuids)
        .reserverId(reservation.getReserverId())
        .build();
  }
}
