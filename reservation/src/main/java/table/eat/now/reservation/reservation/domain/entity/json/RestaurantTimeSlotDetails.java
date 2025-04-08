/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.json;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantTimeSlotDetails {
  private String availableDate;
  private String timeslot;

  @Builder
  public RestaurantTimeSlotDetails(
      String availableDate,
      String timeslot
  ) {
    this.availableDate = availableDate;
    this.timeslot = timeslot;
  }
}