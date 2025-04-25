package table.eat.now.promotion.promotion.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.promotion.promotion.domain.entity.vo.DiscountPrice;
import table.eat.now.promotion.promotion.domain.entity.vo.PromotionDetails;
import table.eat.now.promotion.promotion.domain.entity.vo.PromotionPeriod;

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

  @Column(nullable = false, unique = true, length = 100, name = "promotion_uuid")
  private String promotionUuid;

  @Column(length = 100, name = "coupon_uuid")
  private String couponUuid;

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


  private Promotion(
      String couponUuid, String promotionName, String description,
      LocalDateTime startTime, LocalDateTime endTime,
      BigDecimal amount, PromotionStatus promotionStatus, PromotionType promotionType) {
    this.promotionUuid = UUID.randomUUID().toString();
    this.couponUuid = couponUuid;
    this.details = PromotionDetails.of(promotionName, description);
    this.period = PromotionPeriod.of(startTime,endTime);
    this.discountPrice = DiscountPrice.of(amount);
    this.promotionStatus = promotionStatus;
    this.promotionType = promotionType;
  }

  public static Promotion of(
      String couponUuid, String promotionName, String description,
      LocalDateTime startTime, LocalDateTime endTime,
      BigDecimal amount, PromotionStatus promotionStatus, PromotionType promotionType) {
    return new Promotion(
        couponUuid, promotionName, description, startTime, endTime,
        amount,promotionStatus,promotionType);
  }

  public void modifyPromotion(
      String couponUuid, String promotionName, String description, LocalDateTime startTime,
      LocalDateTime endTime, BigDecimal amount,
      PromotionStatus promotionStatus, PromotionType promotionType) {
    this.couponUuid = couponUuid;
    this.details = PromotionDetails.of(promotionName, description);
    this.period = PromotionPeriod.of(startTime,endTime);
    this.discountPrice = DiscountPrice.of(amount);
    this.promotionStatus = promotionStatus;
    this.promotionType = promotionType;
  }

  public void modifyPromotionStatus(PromotionStatus status) {
    switch (status) {
      case READY -> this.promotionStatus = PromotionStatus.RUNNING;
      case RUNNING ->this.promotionStatus = PromotionStatus.CLOSED;
    }
  }
}
