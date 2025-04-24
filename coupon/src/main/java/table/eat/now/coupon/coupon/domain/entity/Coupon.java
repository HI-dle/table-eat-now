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
import table.eat.now.coupon.coupon.domain.command.UpdateCoupon;

@Table(name="p_coupon")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="coupon_uuid", unique = true, nullable = false, length = 100)
  private String couponUuid;

  @Column(nullable = false, length = 200)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CouponType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CouponLabel label;

  @Embedded
  private AvailablePeriod period;

  @Column(nullable = false)
  private Integer count;

  @Column(nullable = false)
  private Integer issuedCount;

  @Column(nullable = false)
  private Boolean allowDuplicate;

  @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DiscountPolicy> policy;

  private Coupon(
      String name, CouponType type, CouponLabel label,
      LocalDateTime issueStartAt, LocalDateTime issueEndAt, LocalDateTime expireAt,
      Integer validDays, Integer count, Boolean allowDuplicate) {
    this.couponUuid = UUID.randomUUID().toString();
    this.name = name;
    this.type = type;
    this.label = label;
    this.period = AvailablePeriod.of(issueStartAt, issueEndAt, expireAt, validDays, label);
    this.count = count;
    this.issuedCount = 0;
    this.allowDuplicate = allowDuplicate;
    this.policy = new ArrayList<>();
  }

  public static Coupon of(
      String name, String type, String label,
      LocalDateTime issueStartAt, LocalDateTime issueEndAt, LocalDateTime expireAt,
      Integer validDays, Integer count, Boolean allowDuplicate) {

    CouponType typeE = CouponType.parse(type.toUpperCase());
    CouponLabel labelE = CouponLabel.parse(label.toUpperCase());

    return new Coupon(name, typeE, labelE, issueStartAt, issueEndAt, expireAt, validDays, count, allowDuplicate);
  }

  public DiscountPolicy getDiscountPolicy() {
    return policy.get(0);
  }

  public void registerPolicy(DiscountPolicy policy) {
    policy.registerCoupon(this);
    this.policy.add(policy);
  }

  public void modify(UpdateCoupon command) {

    if (!period.is2HourBeforeIssueStartAt()) {
      throw new IllegalArgumentException("쿠폰 가용 시각으로부터 2 시간 이전까지만 수정이 가능합니다.");
    }
    this.name = command.name();
    this.type = command.type();
    this.label = command.label();
    this.period = AvailablePeriod.of(
        command.issueStartAt(), command.issueEndAt(), command.expireAt(), command.validDays(), command.label());
    this.count = command.count();
    this.allowDuplicate = command.allowDuplicate();

    this.getDiscountPolicy().modify(command);
  }

  @Override
  public void delete(Long deletedBy) {

    if (!(period.is2HourBeforeIssueStartAt()|| period.isAfterIssueEndAt())) {
      throw new IllegalArgumentException("쿠폰 가용 시각으로부터 두 시간 이전이거나 이미 종료된 쿠폰만 삭제가 가능합니다.");
    }
    super.delete(deletedBy);
    getDiscountPolicy().delete(deletedBy);
  }

  public boolean hasStockCount() {
    return getCount() > 0;
  }

  public void calcIssuedCount(Integer remainder) {
    this.issuedCount = remainder == null
        ? this.issuedCount
        : remainder <= 0 ? this.count : this.count - remainder;
  }
}
