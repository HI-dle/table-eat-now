/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.entity.json;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantMenuDetails {
  private String name;
  private BigDecimal price;

  @Builder
  private RestaurantMenuDetails(String name, BigDecimal price) {
    this.name = name;
    this.price = price;
  }
}
