package table.eat.now.coupon.coupon.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import table.eat.now.common.domain.BaseEntity;

@Table(name="p_coupon_policy")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPolicy extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "coupon_uuid", referencedColumnName = "coupon_uuid", unique = true)
  private Coupon coupon;

  @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
  private UUID couponPolicyUuid;

  @Column(nullable = false)
  private Integer minPurchaseAmount;

  @Column
  private Integer amount;

  @Column
  private Integer percent;

  @Column
  private Integer maxDiscountAmount;

  private CouponPolicy(
      @NonNull Integer minPurchaseAmount,
      Integer amount, Integer percent, Integer maxDiscountAmount) {

    this.couponPolicyUuid = UUID.randomUUID();
    this.minPurchaseAmount = minPurchaseAmount;

    if (amount == null && percent == null) {
      throw new IllegalArgumentException("할인 금액과 할인율이 둘 다 빈 값일 수 없습니다.");
    }
    this.amount = amount;
    this.percent = percent;
    this.maxDiscountAmount = maxDiscountAmount;
  }

  public static CouponPolicy of(
      Integer minPurchaseAmount,
      Integer amount, Integer percent, Integer maxDiscountAmount) {

    return new CouponPolicy(minPurchaseAmount, amount, percent, maxDiscountAmount);
  }

  public void registerCoupon(Coupon coupon) {
    this.coupon = coupon;
  }
}
