/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.json;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantTimeSlotDetails {
  private String availableDate;
  private String timeslot;

  public static RestaurantTimeSlotDetails of(String availableDate, String timeslot) {
    return new RestaurantTimeSlotDetails(availableDate, timeslot);
  }
}