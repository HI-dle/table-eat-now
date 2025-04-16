/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.json;

import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantDetails {
  private String name;
  private String address;
  private Long ownerId;
  private Long staffId;
  private String contactNumber;
  private LocalTime openingTime;
  private LocalTime closingTime;

  public static RestaurantDetails of(
      String name,
      String address,
      Long ownerId,
      Long staffId,
      String contactNumber,
      LocalTime openingTime,
      LocalTime closingTime
      ) {
    return new RestaurantDetails(name, address, ownerId, staffId, contactNumber, openingTime, closingTime);
  }
}
