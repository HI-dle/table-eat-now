/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetRestaurantInfo.Timeslot;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;

@Component
public class ValidRestaurantAvailability implements ValidItem<CreateReservationValidationContext> {

  //  검증 (시간/날짜/인원/메뉴 등)
  @Override
  public void validate(CreateReservationValidationContext ctx) {
    CreateReservationCommand command = ctx.command();
    GetRestaurantInfo restaurant = ctx.restaurant();

    Map<String, Timeslot> timeslotMap = restaurant.timeslots().stream()
        .collect(Collectors.toMap(Timeslot::restaurantTimeslotUuid, timeslot -> timeslot));

    Timeslot timeslot = timeslotMap.get(command.restaurantTimeslotUuid());

    if (timeslot == null) {
      throw CustomException.from(ReservationErrorCode.INVALID_TIMESLOT);
    }

    // 날짜 확인
    validateDate(timeslot, command.restaurantTimeSlotDetails().availableDate());

    // 시간 확인
    validateTime(timeslot, command.restaurantTimeSlotDetails().timeslot());

    // 인원 수 확인
    validateGuestCapacity(timeslot, command.guestCount());

    // 메뉴 확인
    validateRestaurantMenu(restaurant, command);
  }

  private static void validateDate(Timeslot timeslot, LocalDate commandAvailableDate) {
    boolean isDateMatch = timeslot.availableDate()
        .equals(commandAvailableDate);
    if (!isDateMatch) {
      throw CustomException.from(ReservationErrorCode.INVALID_RESERVATION_DATE);
    }
  }

  private static void validateTime(Timeslot timeslot, LocalTime commandTimeSlot) {
    boolean isTimeMatch = timeslot.timeslot()
        .equals(commandTimeSlot);
    if (!isTimeMatch) {
      throw CustomException.from(ReservationErrorCode.INVALID_RESERVATION_TIME);
    }
  }

  private static void validateGuestCapacity(Timeslot timeslot, Integer guestCount) {
    boolean exceedsCapacity =
        guestCount + timeslot.curTotalGuestCount() > timeslot.maxCapacity();
    if (exceedsCapacity) {
      throw CustomException.from(ReservationErrorCode.EXCEEDS_MAX_GUEST_CAPACITY);
    }
  }

  private static void validateRestaurantMenu(GetRestaurantInfo restaurant, CreateReservationCommand command) {
    boolean validMenu = restaurant.menus().stream().anyMatch(menu ->
        menu.restaurantMenuUuid().equals(command.restaurantMenuUuid()) &&
            menu.name().equals(command.restaurantMenuDetails().name()) &&
            menu.price().compareTo(command.restaurantMenuDetails().price()) == 0
    );
    if (!validMenu) {
      throw CustomException.from(ReservationErrorCode.INVALID_MENU_SELECTION);
    }
  }
}
