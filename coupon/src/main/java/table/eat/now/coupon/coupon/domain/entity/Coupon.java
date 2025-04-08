package table.eat.now.coupon.coupon.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Table(name="p_coupon")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="coupon_uuid", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
  private UUID couponUuid;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CouponType type;

  @Embedded
  private AvailablePeriod period;

  @Column
  private Integer count;

  @Column
  private Integer issuedCount;

  @Column(nullable = false)
  private Boolean allowDuplicate;

  @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DiscountPolicy> policy;

  private Coupon(
      String name, CouponType type, LocalDateTime startAt, LocalDateTime endAt,
      Integer count, Boolean allowDuplicate) {
    this.couponUuid = UUID.randomUUID();
    this.name = name;
    this.type = type;
    this.period = new AvailablePeriod(startAt, endAt);
    this.count = count;
    this.issuedCount = 0;
    this.allowDuplicate = allowDuplicate;
    this.policy = new ArrayList<>();
  }

  public static Coupon of(
      String name, CouponType type, LocalDateTime startAt, LocalDateTime endAt,
      Integer count, Boolean allowDuplicate) {

    return new Coupon(name, type, startAt, endAt, count, allowDuplicate);
  }

  public void registerPolicy(DiscountPolicy policy) {
    policy.registerCoupon(this);
    this.policy.add(policy);
  }
}
