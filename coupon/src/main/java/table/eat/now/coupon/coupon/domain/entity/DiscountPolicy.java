package table.eat.now.coupon.coupon.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import table.eat.now.coupon.coupon.domain.command.UpdateCoupon;

@Table(name="p_coupon_policy")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountPolicy extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "coupon_uuid", referencedColumnName = "coupon_uuid", unique = true)
  private Coupon coupon;

  @Column(unique = true, nullable = false, length = 100)
  private String discountPolicyUuid;

  @Column(nullable = false)
  private Integer minPurchaseAmount;

  @Column
  private Integer amount;

  @Column
  private Integer percent;

  @Column
  private Integer maxDiscountAmount;

  private DiscountPolicy(
      Integer minPurchaseAmount, Integer amount, Integer percent, Integer maxDiscountAmount) {

    this.discountPolicyUuid = UUID.randomUUID().toString();
    defineDiscountValue(minPurchaseAmount, amount, percent, maxDiscountAmount);
  }

  public static DiscountPolicy of(
      Integer minPurchaseAmount, Integer amount, Integer percent, Integer maxDiscountAmount) {

    return new DiscountPolicy(minPurchaseAmount, amount, percent, maxDiscountAmount);
  }

  public void modify(UpdateCoupon command) {

    defineDiscountValue(
        command.minPurchaseAmount(), command.amount(), command.percent(), command.maxDiscountAmount());
  }

  public void registerCoupon(Coupon coupon) {
    this.coupon = coupon;
  }

  private void defineDiscountValue(
      @NonNull Integer minPurchaseAmount, Integer amount, Integer percent, Integer maxDiscountAmount) {

    this.minPurchaseAmount = minPurchaseAmount;

    validateDiscountInfo(amount, percent, maxDiscountAmount);
    this.amount = amount;
    this.percent = percent;
    this.maxDiscountAmount = maxDiscountAmount;
  }

  private void validateDiscountInfo(Integer amount, Integer percent, Integer maxDiscountAmount) {

    if (amount == null && percent == null) {
      throw new IllegalArgumentException("할인 금액과 할인율이 둘 다 빈 값일 수 없습니다.");
    }
    if (amount != null && percent != null) {
      throw new IllegalArgumentException("할인 금액과 할인률 중 한 가지만 입력할 수 있습니다.");
    }
    if (percent != null && maxDiscountAmount == null) {
      throw new IllegalArgumentException("정률 쿠폰에는 최대 할인금액이 설정되어야 합니다.");
    }
  }
}
