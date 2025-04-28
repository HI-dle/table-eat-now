package table.eat.now.promotion.promotion.domain.entity.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 26.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaxParticipant {

  private int maxParticipantsValue;

  public static MaxParticipant of(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException("최대 참여 인원은 0보다 커야 합니다.");
    }
    if (value % 1000 != 0) {
      throw new IllegalArgumentException("최대 참여 인원은 1000으로 나누어 떨어져야 합니다.");
    }
    return new MaxParticipant(value);
  }

}
