/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;

public record CreateReservationRequest(

    @NotBlank(message = "예약자 이름(reserverName)은 필수입니다.")
    String reserverName,

    @NotBlank(message = "예약자 연락처(reserverContact)는 필수입니다.")
    String reserverContact,

    @NotNull(message = "레스토랑 (restaurantUuid)는 필수입니다.")
    String restaurantUuid,

    @NotNull(message = "레스토랑 타임슬롯 (restaurantTimeslotUuid)는 필수입니다.")
    String restaurantTimeslotUuid,

    @NotNull(message = "레스토랑 메뉴 (restaurantMenuUuid)는 필수입니다.")
    String restaurantMenuUuid,

    @NotNull(message = "예약 인원 수(guestCount)는 필수입니다.")
    @Min(value = 1, message = "예약 인원 수(guestCount)는 최소 1명 이상이어야 합니다.")
    Integer guestCount,

    @Size(max = 1000, message = "요청사항(specialRequest)은 최대 1000자까지 입력할 수 있습니다.")
    String specialRequest,

    @NotNull(message = "총 금액(totalPrice)은 필수입니다.")
    @Positive(message = "총 금액(totalPrice)은 0보다 커야 합니다.")
    BigDecimal totalPrice,

    @NotNull(message = "레스토랑 타임슬롯 상세 정보(restaurantTimeSlotDetails)는 필수입니다.")
    @Valid
    RestaurantTimeSlotDetails restaurantTimeSlotDetails,

    @NotNull(message = "레스토랑 상세 정보(restaurantDetails)는 필수입니다.")
    @Valid
    RestaurantDetails restaurantDetails,

    @NotNull(message = "레스토랑 메뉴 상세 정보(restaurantMenuDetails)는 필수입니다.")
    @Valid
    RestaurantMenuDetails restaurantMenuDetails,

    @NotEmpty(message = "결제 정보(payments)는 최소 1개 이상 필요합니다.")
    @Valid
    List<PaymentDetail> payments

) {

  public CreateReservationCommand toCommand(Long reserverId, LocalDateTime reservationDate) {
    return CreateReservationCommand.builder()
        .reserverId(reserverId)
        .reserverName(reserverName)
        .reserverContact(reserverContact)
        .restaurantUuid(restaurantUuid)
        .restaurantTimeslotUuid(restaurantTimeslotUuid)
        .restaurantMenuUuid(restaurantMenuUuid)
        .guestCount(guestCount)
        .specialRequest(specialRequest)
        .restaurantTimeSlotDetails(restaurantTimeSlotDetails.toCommandRestaurantTimeSlotDetails())
        .restaurantDetails(restaurantDetails.toCommandRestaurantDetails())
        .restaurantMenuDetails(restaurantMenuDetails.toCommandRestaurantMenuDetails())
        .payments(
            payments.stream()
                .map(PaymentDetail::toCommandPaymentDetail)
                .toList()
        )
        .reservationDate(reservationDate)
        .build();
  }

  public record RestaurantTimeSlotDetails(
      @NotBlank(message = "예약 가능 날짜(availableDate)는 필수입니다.")
      LocalDate availableDate,

      @NotBlank(message = "타임슬롯(timeslot)은 필수입니다.")
      LocalTime timeslot
  ) {

    public CreateReservationCommand.RestaurantTimeSlotDetails toCommandRestaurantTimeSlotDetails() {
      return CreateReservationCommand.RestaurantTimeSlotDetails.builder()
          .availableDate(availableDate)
          .timeslot(timeslot)
          .build();
    }
  }

  public record RestaurantDetails(
      @NotBlank(message = "레스토랑 이름(name)은 필수입니다.")
      String name,

      @NotBlank(message = "레스토랑 주소(address)는 필수입니다.")
      String address,

      @NotBlank(message = "연락처(contactNumber)는 필수입니다.")
      String contactNumber,

      @NotBlank(message = "오픈 시간(openingAt)은 필수입니다.")
      String openingAt,

      @NotBlank(message = "마감 시간(closingAt)은 필수입니다.")
      String closingAt
  ) {

    public CreateReservationCommand.RestaurantDetails toCommandRestaurantDetails() {
      return CreateReservationCommand.RestaurantDetails.builder()
          .name(name)
          .address(address)
          .contactNumber(contactNumber)
          .openingAt(openingAt)
          .closingAt(closingAt)
          .build();
    }
  }

  public record RestaurantMenuDetails(
      @NotBlank(message = "메뉴 이름(name)은 필수입니다.")
      String name,

      @NotNull(message = "메뉴 가격(price)은 필수입니다.")
      @Positive(message = "메뉴 가격(price)은 0보다 커야 합니다.")
      BigDecimal price,

      @NotNull(message = "메뉴 수량(quantity)은 필수입니다.")
      @Positive(message = "메뉴 수량(quantity)은 0보다 커야 합니다.")
      Integer quantity
  ) {

    public CreateReservationCommand.RestaurantMenuDetails toCommandRestaurantMenuDetails() {
      return CreateReservationCommand.RestaurantMenuDetails.builder()
          .name(name)
          .price(price)
          .quantity(quantity)
          .build();
    }
  }

  public record PaymentDetail(

      @NotNull(message = "결제 유형(type)은 필수입니다.")
      PaymentType type,

      @NotNull(message = "결제 참조 ID(detailReferenceId)는 필수입니다.")
      String detailReferenceId,

      @NotNull(message = "결제 금액(amount)은 필수입니다.")
      @Positive(message = "결제 금액(amount)은 0보다 커야 합니다.")
      BigDecimal amount

  ) {

    public CreateReservationCommand.PaymentDetail toCommandPaymentDetail() {
      return CreateReservationCommand.PaymentDetail.builder()
          .type(CreateReservationCommand.PaymentDetail.PaymentType.valueOf(type.name()))
          .detailReferenceId(detailReferenceId)
          .amount(amount)
          .build();
    }
  }

  public enum PaymentType {
    PAYMENT,
    PROMOTION_COUPON,
    PROMOTION_EVENT
  }
}