/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 18.
 */
package table.eat.now.restaurant.restaurant.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand.TimeSlotCommand;

public record ModifyRestaurantRequest(
    @NotBlank(message = "식당 이름은 필수입니다.")
    String name,

    @NotBlank(message = "주소는 필수입니다.")
    String address,

    @NotBlank(message = "연락처는 필수입니다.")
    String contactNumber,

    @NotNull(message = "오픈 시간은 필수입니다.")
    LocalDateTime openingAt,

    @NotNull(message = "마감 시간은 필수입니다.")
    LocalDateTime closingAt,

    @NotBlank(message = "소개 정보는 필수입니다.")
    String info,

    @NotBlank(message = "상태는 필수입니다.")
    @Pattern(regexp = "OPENED|CLOSED|BREAK|HOLIDAY|INACTIVE", message = "유효하지 않은 예약 상태입니다.")
    String status,

    @NotNull(message = "최대 예약 인원 수는 필수입니다.")
    @Positive(message = "최대 예약 인원 수는 1 이상이어야 합니다.")
    Integer maxReservationGuestCountPerTeamOnline,

    @NotBlank(message = "웨이팅 상태는 필수입니다.")
    @Pattern(regexp = "OPENED|LIMITED|INACTIVE", message = "유효하지 않은 프로모션 상태입니다.")
    String waitingStatus,

    @Size(max = 100, message = "메뉴는 최대 100개까지 등록 가능합니다.")
    @Valid
    List<MenuRequest> menus,

    @Size(max = 300, message = "타임슬롯은 최대 300개까지 등록 가능합니다.")
    @Valid
    List<TimeslotRequest> timeslots
) {

  public ModifyRestaurantCommand toCommand(
      String restaurantId, Long requesterId, UserRole requesterRole) {
    return ModifyRestaurantCommand.builder()
        .requesterId(requesterId)
        .requesterRole(requesterRole)
        .restaurantUuid(restaurantId)
        .name(name)
        .address(address)
        .contactNumber(contactNumber)
        .openingAt(openingAt.toLocalTime())
        .closingAt(closingAt.toLocalTime())
        .info(info)
        .status(status)
        .waitingStatus(waitingStatus)
        .maxReservationGuestCountPerTeamOnline(maxReservationGuestCountPerTeamOnline)
        .menus(menus.stream().map(MenuRequest::toCommand).toList())
        .timeslots(timeslots.stream().map(TimeslotRequest::toCommand).toList())
        .build();
  }

  public record MenuRequest(
      String restaurantMenuUuid,

      @NotBlank(message = "메뉴 이름은 필수입니다.")
      String name,

      @NotNull(message = "가격은 필수입니다.")
      @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다.")
      BigDecimal price,

      @NotBlank(message = "메뉴 상태는 필수입니다.")
      @Pattern(regexp = "ACTIVE|INACTIVE|SOLDOUT", message = "유효하지 않은 프로모션 상태입니다.")
      String status
  ) {
    public ModifyRestaurantCommand.MenuCommand toCommand() {
      return ModifyRestaurantCommand.MenuCommand.builder()
          .restaurantMenuUuid(restaurantMenuUuid)
          .name(name)
          .price(price)
          .status(status)
          .build();
    }
  }

  public record TimeslotRequest(
      String restaurantTimeslotUuid,

      @NotNull(message = "예약 가능 시작일은 필수입니다.")
      LocalDate availableStartDate,

      @NotNull(message = "최대 수용 인원은 필수입니다.")
      @Positive(message = "최대 수용 인원은 1 이상이어야 합니다.")
      Integer maxCapacity,

      @NotNull(message = "타임슬롯 시간은 필수입니다.")
      LocalTime timeslot
  ) {
    public TimeSlotCommand toCommand() {
      return TimeSlotCommand.builder()
          .restaurantTimeslotUuid(restaurantTimeslotUuid)
          .availableDate(availableStartDate)
          .timeslot(timeslot)
          .maxCapacity(maxCapacity)
          .build();
    }
  }
}