package table.eat.now.promotion.promotion.domain.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class PromotionDetails {

  @Column(length = 500, nullable = false)
  private String promotionName;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  public static PromotionDetails of(String promotionName, String description) {
    return new PromotionDetails(promotionName, description);
  }
}
