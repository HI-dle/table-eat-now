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
    Long reserverId,
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
    String status,
    String specialRequest,
    BigDecimal totalAmount,
    List<PaymentDetailDto> paymentDetails
) {
  public static GetReservationResponse from(GetReservationInfo info) {
    return new GetReservationResponse(
        info.reservationUuid(),
        info.name(),
        info.reserverId(),
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
        info.status().name(),
        info.specialRequest(),
        info.totalAmount(),
        info.paymentDetails().stream()
            .map(PaymentDetailDto::from)
            .toList()
    );
  }

  public record PaymentDetailDto(
      String reservationPaymentDetailUuid,
      String type,
      BigDecimal amount,
      String detailReferenceId
  ) {
    public static PaymentDetailDto from(GetReservationInfo.PaymentDetailInfo detail) {
      return new PaymentDetailDto(
          detail.reservationPaymentDetailUuid(),
          detail.type().name(),
          detail.amount(),
          detail.detailReferenceId()
      );
    }
  }
}