/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.reservation.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.reservation.reservation.application.service.dto.response.GetReservationInfo;

public record GetReservationResponse(
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
    GetReservationInfo.ReservationStatus status,
    String specialRequest,
    BigDecimal totalAmount,
    List<PaymentDetailDto> paymentDetails
) {
  public static GetReservationResponse from(GetReservationInfo info) {
    return new GetReservationResponse(
        info.reservationUuid(),
        info.name(),
        info.reserverName(),
        info.reserverContact(),
        info.guestCount(),
        info.restaurantName(),
        info.restaurantContactNumber(),
        info.restaurantAddress(),
        info.reservationDate(),
        info.reservationTime(),
        info.menuName(),
        info.menuPrice(),
        info.menuQuantity(),
        info.status(),
        info.specialRequest(),
        info.totalAmount(),
        info.paymentDetails().stream()
            .map(PaymentDetailDto::from)
            .toList()
    );
  }

  public record PaymentDetailDto(
      String reservationPaymentDetailUuid,
      GetReservationInfo.PaymentType type,
      BigDecimal amount,
      String detailReferenceId
  ) {
    public static PaymentDetailDto from(GetReservationInfo.PaymentDetailInfo detail) {
      return new PaymentDetailDto(
          detail.reservationPaymentDetailUuid(),
          detail.type(),
          detail.amount(),
          detail.detailReferenceId()
      );
    }
  }
}