package table.eat.now.promotion.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.promotion.domain.entity.vo.DiscountPrice;
import table.eat.now.promotion.domain.entity.vo.PromotionDetails;
import table.eat.now.promotion.domain.entity.vo.PromotionPeriod;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Entity
@Table(name = "p_promotion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
  private UUID promotionUuid;

  @Embedded
  private PromotionDetails details;

  @Embedded
  private PromotionPeriod period;

  @Embedded
  private DiscountPrice discountPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PromotionStatus promotionStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PromotionType promotionType;

  @Builder
  public Promotion(PromotionDetails details, PromotionPeriod period,
      DiscountPrice discountPrice, PromotionStatus promotionStatus, PromotionType promotionType) {
    this.promotionUuid = UUID.randomUUID();
    this.details = details;
    this.period = period;
    this.discountPrice = discountPrice;
    this.promotionStatus = promotionStatus;
    this.promotionType = promotionType;
  }
}
