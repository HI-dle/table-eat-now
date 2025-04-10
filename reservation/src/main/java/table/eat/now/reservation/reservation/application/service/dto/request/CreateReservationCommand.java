/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.entity.ReservationPaymentDetail;

@Builder
public record CreateReservationCommand(
    Long reserverId,
    String reserverName,
    String reserverContact,
    String restaurantUuid,
    String restaurantTimeslotUuid,
    String restaurantMenuUuid,
    Integer guestCount,
    String specialRequest,
    RestaurantTimeSlotDetails restaurantTimeSlotDetails,
    RestaurantDetails restaurantDetails,
    RestaurantMenuDetails restaurantMenuDetails,
    List<PaymentDetail> payments
) {

  public Reservation toEntityWithPaymentKey(String paymentKey) {
    List<ReservationPaymentDetail> paymentDetails = this.payments().stream()
        .map(paymentDetail -> paymentDetail.toEntityWithPaymentKey(paymentKey))
        .toList();

    Reservation reservation = Reservation.builder()
        .reserverId(reserverId)
        .restaurantTimeSlotUuid(restaurantTimeslotUuid)
        .reservationDate(restaurantTimeSlotDetails.availableDate())
        .reservationTimeslot(restaurantTimeSlotDetails.timeslot())
        .restaurantId(restaurantUuid)
        .restaurantName(restaurantDetails.name())
        .restaurantAddress(restaurantDetails.address())
        .restaurantContactNumber(restaurantDetails.contactNumber())
        .restaurantOpeningAt(restaurantDetails.openingAt())
        .restaurantClosingAt(restaurantDetails.closingAt())
        .menuName(restaurantMenuDetails.name())
        .menuQuantity(restaurantMenuDetails.quantity())
        .menuPrice(restaurantMenuDetails.price())
        .reserverName(reserverName)
        .reserverContact(reserverContact)
        .guestCount(guestCount)
        .status(Reservation.ReservationStatus.PENDING_PAYMENT)
        .specialRequest(specialRequest)
        .details(paymentDetails)
        .build();

    return reservation;
  }


  @Builder
  public record RestaurantTimeSlotDetails(String availableDate, String timeslot) {

  }

  @Builder
  public record RestaurantDetails(
      String name,
      String address,
      String contactNumber,
      String openingAt,
      String closingAt
  ) {

  }

  @Builder
  public record RestaurantMenuDetails(String name, BigDecimal price, Integer quantity) {

  }

  @Builder
  public record PaymentDetail(
      PaymentType type,
      String detailReferenceId,
      BigDecimal amount
  ) {

    public ReservationPaymentDetail toEntityWithPaymentKey(String paymentKey) {
      return ReservationPaymentDetail.builder()
          .amount(amount)
          .detailReferenceId(type == PaymentType.PAYMENT ? paymentKey : detailReferenceId)
          .type(ReservationPaymentDetail.PaymentType.valueOf(type.name()))
          .build();
    }

    public enum PaymentType {
      PAYMENT,
      PROMOTION_COUPON,
      PROMOTION_EVENT
    }
  }
}