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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Table(name="p_user_coupon")
@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 100)
  private String userCouponUuid;

  @Column(nullable = false, length = 100)
  private String couponUuid;

  @Column(nullable = false)
  Long userId;

  @Column(length = 100)
  private String reservationUuid;

  @Column(length = 200)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserCouponStatus status;

  @Column
  private LocalDateTime expiresAt;

  @Column
  private LocalDateTime preemptAt;

  @Column
  private LocalDateTime usedAt;

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
}
