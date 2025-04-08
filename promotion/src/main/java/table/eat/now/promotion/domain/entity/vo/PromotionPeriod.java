package table.eat.now.promotion.domain.entity.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class PromotionPeriod {

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public PromotionPeriod(LocalDateTime startTime, LocalDateTime endTime) {
    if (endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
    }
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
