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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantDetails {
  private String name;
  private String address;
  private String contactNumber;
  private String openingAt;
  private String closingAt;

  public static RestaurantDetails of(String address, String closingAt, String contactNumber, String name, String openingAt) {
    return new RestaurantDetails(address, closingAt, contactNumber, name, openingAt);
  }
}
