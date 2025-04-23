package table.eat.now.coupon.user_coupon.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.coupon.user_coupon.domain.exception.UserCouponDomainErrorCode;
import table.eat.now.coupon.user_coupon.domain.exception.UserCouponException;

@Table(name="p_user_coupon")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false, length = 100)
  private String userCouponUuid;

  @Column(nullable = false, length = 100)
  private String couponUuid;

  @Column(nullable = false)
  Long userId;

  @Column(length = 100)
  private String reservationUuid;

  @Column(length = 200, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserCouponStatus status;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @Column
  private LocalDateTime preemptAt;

  @Column
  private LocalDateTime usedAt;

  @Builder
  private UserCoupon(String userCouponUuid, String couponUuid, Long userId,
      String name, LocalDateTime expiresAt, UserCouponStatus status) {
    this.userCouponUuid = userCouponUuid;
    this.couponUuid = couponUuid;
    this.userId = userId;
    this.name = name;
    this.expiresAt = expiresAt;
    this.status = status;
  }

  public static UserCoupon of(String userCouponUuid, String couponUuid, Long userId,
      String name, LocalDateTime expiresAt) {

    return UserCoupon.builder()
        .userCouponUuid(userCouponUuid)
        .couponUuid(couponUuid)
        .userId(userId)
        .name(name)
        .expiresAt(expiresAt)
        .status(UserCouponStatus.ISSUED)
        .build();
  }

  public void isOwnedBy(Long userId) {
    if (!this.userId.equals(userId)) {
      throw UserCouponException.from(UserCouponDomainErrorCode.UNAUTH_USER_COUPON);
    }
  }

  public void isValidToPreempt(String reservationUuid) {
    if (this.expiresAt.isBefore(LocalDateTime.now())) {
      throw UserCouponException.from(UserCouponDomainErrorCode.EXPIRED_USER_COUPON);
    }
    if (this.usedAt != null) {
      throw UserCouponException.from(UserCouponDomainErrorCode.ALREADY_USED_USER_COUPON);
    }
    if (this.preemptAt != null && !this.reservationUuid.equals(reservationUuid)) {
      throw UserCouponException.from(UserCouponDomainErrorCode.PREEMPT_USER_COUPON);
    }
  }

  public void preempt(String reservationUuid) {
    this.status = UserCouponStatus.PREEMPT;
    this.reservationUuid = reservationUuid;
    this.preemptAt = LocalDateTime.now();
  }

  public void use() {
    this.status = UserCouponStatus.COMMIT;
    this.usedAt = LocalDateTime.now();
  }

  public void release() {
    this.status = UserCouponStatus.ROLLBACK;
    this.reservationUuid = null;
    this.preemptAt = null;
    this.usedAt = null;
  }
}
