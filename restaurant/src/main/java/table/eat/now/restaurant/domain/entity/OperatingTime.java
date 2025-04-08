package table.eat.now.restaurant.domain.entity;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperatingTime {
  private LocalDateTime openingAt;
  private LocalDateTime closingAt;
}

