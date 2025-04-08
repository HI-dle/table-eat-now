package table.eat.now.promotion.domain.entity.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionPeriod {

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  public static PromotionPeriod of(LocalDateTime startTime, LocalDateTime endTime) {
    if (endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
    }
    return new PromotionPeriod(startTime, endTime);
  }
}
