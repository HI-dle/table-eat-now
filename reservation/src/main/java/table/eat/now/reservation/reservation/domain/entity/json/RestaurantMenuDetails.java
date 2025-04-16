/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.json;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantMenuDetails {
  private String name;
  private BigDecimal price;
  private Integer quantity;

  public static RestaurantMenuDetails of(String name, BigDecimal price, Integer quantity) {
    return new RestaurantMenuDetails(name, price, quantity);
  }
}
