/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 08.
 */
package table.eat.now.restaurant.restaurant.domain.entity.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperatingTime {
  private LocalDateTime openingAt;
  private LocalDateTime closingAt;

  public static OperatingTime of(LocalDateTime openingAt, LocalDateTime closingAt) {
    return new OperatingTime(openingAt, closingAt);
  }
}

