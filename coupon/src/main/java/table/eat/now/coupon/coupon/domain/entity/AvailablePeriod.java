package table.eat.now.coupon.coupon.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class AvailablePeriod {

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private LocalDateTime endAt;

  public AvailablePeriod(LocalDateTime startAt, LocalDateTime endAt) {

    validatePeriod(startAt, endAt);

    this.startAt = startAt;
    this.endAt = endAt;
  }

  private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {

    if (startAt == null || endAt == null) {
      throw new IllegalArgumentException("기간 정보는 필수입니다.");
    }
    if (!is1HourBeforeStartAt(startAt)) {
      throw new IllegalArgumentException("시작일은 현재로부터 1시간 이후부터 가능합니다.");
    }
    if (!startAt.isBefore(endAt)) {
      throw new IllegalArgumentException("시작일이 종료일보다 나중일 수 없습니다.");
    }
  }

  private boolean is1HourBeforeStartAt(LocalDateTime startAt) {
    LocalDateTime now = LocalDateTime.now();
    return now.isBefore(startAt.minusHours(1));
  }
}
