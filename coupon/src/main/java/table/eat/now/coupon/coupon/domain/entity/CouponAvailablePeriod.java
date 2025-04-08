package table.eat.now.coupon.coupon.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CouponAvailablePeriod {

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private LocalDateTime endAt;

  public CouponAvailablePeriod(LocalDateTime startAt, LocalDateTime endAt) {
    if (startAt == null || endAt == null) {
      throw new IllegalArgumentException("기간 정보는 필수입니다.");
    }
    if (!startAt.isBefore(endAt)) {
      throw new IllegalArgumentException("시작일이 종료일보다 나중일 수 없습니다.");
    }
    this.startAt = startAt;
    this.endAt = endAt;
  }
}
