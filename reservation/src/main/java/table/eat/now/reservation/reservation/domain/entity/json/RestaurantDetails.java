package table.eat.now.reservation.reservation.domain.entity.json;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantDetails {
  private String name;
  private String address;
  private String contactNumber;
  private String openingAt;
  private String closingAt;

  @Builder
  private RestaurantDetails(
      String address,
      String closingAt,
      String contactNumber,
      String name,
      String openingAt
  ) {
    this.address = address;
    this.closingAt = closingAt;
    this.contactNumber = contactNumber;
    this.name = name;
    this.openingAt = openingAt;
  }
}
