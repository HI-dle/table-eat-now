/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;

public record CreateRestaurantRequest (
    @NotNull(message = "ownerId는 필수값입니다.")
    Long ownerId,

    @NotBlank(message = "상호명(name)은 필수값입니다.")
    String name,

    String info,

    @NotNull(message = "온라인 최대 예약 인원 수(maxReservationGuestCountPerTeamOnline)는 필수값입니다.")
    Integer maxReservationGuestCountPerTeamOnline,

    @NotBlank(message = "연락처(contactNumber)는 필수값입니다.")
    String contactNumber,

    @NotBlank(message = "주소(address)는 필수값입니다.")
    String address,

    @NotNull(message = "오픈 시간(openingAt)는 필수값입니다.")
    LocalTime openingAt,

    @NotNull(message = "주소(closingAt)는 필수값입니다.")
    LocalTime closingAt
){

  public CreateRestaurantCommand toCommand() {
    return CreateRestaurantCommand.builder()
        .ownerId(ownerId)
        .name(name)
        .info(info)
        .maxReservationGuestCountPerTeamOnline(maxReservationGuestCountPerTeamOnline)
        .contactNumber(contactNumber)
        .address(address)
        .openingAt(openingAt)
        .closingAt(closingAt)
        .build();
  }
}
