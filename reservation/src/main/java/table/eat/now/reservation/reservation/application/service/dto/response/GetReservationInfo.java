/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.reservation.application.service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;

public record GetReservationInfo(
    String reservationUuid,
    String name,
    String reserverName,
    String reserverContact,
    int guestCount,
    String restaurantName,
    String restaurantContactNumber,
    String restaurantAddress,
    LocalDate reservationDate,
    LocalTime reservationTime,
    String menuName,
    BigDecimal menuPrice,
    int menuQuantity,
    ReservationStatus status,
    String specialRequest,
    BigDecimal totalAmount,
    List<PaymentDetailInfo> paymentDetails
) {

  public static GetReservationInfo from(Reservation reservation) {
    return new GetReservationInfo(
        reservation.getReservationUuid(),
        reservation.getName(),
        reservation.getGuestInfo().getReserverName(),
        reservation.getGuestInfo().getReserverContact(),
        reservation.getGuestInfo().getGuestCount(),
        reservation.getRestaurantDetails().getName(),
        reservation.getRestaurantDetails().getContactNumber(),
        reservation.getRestaurantDetails().getAddress(),
        reservation.getRestaurantTimeSlotDetails().getAvailableDate(),
        reservation.getRestaurantTimeSlotDetails().getTimeslot(),
        reservation.getRestaurantMenuDetails().getName(),
        reservation.getRestaurantMenuDetails().getPrice(),
        reservation.getRestaurantMenuDetails().getQuantity(),
        ReservationStatus.valueOf(reservation.getStatus().toString()),
        reservation.getSpecialRequest(),
        reservation.getTotalAmount(),
        reservation.getPaymentDetails().getValues().stream()
            .map(PaymentDetailInfo::from)
            .toList()
    );
  }

  public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED // 예시
  }

  public record PaymentDetailInfo(
      String reservationPaymentDetailUuid,
      PaymentType type,
      BigDecimal amount,
      String detailReferenceId
  ) {

    public static PaymentDetailInfo from(ReservationPaymentDetail detail) {
      return new PaymentDetailInfo(
          detail.getReservationPaymentDetailUuid(),
          PaymentType.valueOf(detail.getType().getName()),
          detail.getAmount(),
          detail.getDetailReferenceId()
      );
    }
  }

  public enum PaymentType {
    CARD, CASH, COUPON // 예시
  }
}
