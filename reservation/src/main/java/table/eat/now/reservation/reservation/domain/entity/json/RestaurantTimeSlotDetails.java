/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.json;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantTimeSlotDetails {
  private LocalDate availableDate;
  private LocalTime timeslot;

  public static RestaurantTimeSlotDetails of(LocalDate availableDate, LocalTime timeslot) {
    return new RestaurantTimeSlotDetails(availableDate, timeslot);
  }
}