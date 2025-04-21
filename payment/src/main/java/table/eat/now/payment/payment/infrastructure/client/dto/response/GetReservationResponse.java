package table.eat.now.payment.payment.infrastructure.client.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.payment.payment.application.client.dto.GetReservationInfo;

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

  public record PaymentDetailDto(
      String reservationPaymentDetailUuid,
      String type,
      BigDecimal amount,
      String detailReferenceId
  ) {}

  public GetReservationInfo toInfo() {
    return GetReservationInfo.builder()
        .totalAmount(totalAmount)
        .status(status)
        .build();
  }
}