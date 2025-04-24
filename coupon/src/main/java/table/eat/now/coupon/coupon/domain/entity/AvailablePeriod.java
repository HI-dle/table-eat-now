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

  @Column
  private Integer validDays;

  public AvailablePeriod(LocalDateTime startAt, LocalDateTime endAt, Integer validDays) {

    validatePeriod(startAt, endAt, validDays);

    this.startAt = startAt;
    this.endAt = endAt;
    this.validDays = validDays;
  }

  private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt, Integer validDays) {

    if (startAt == null || endAt == null) {
      throw new IllegalArgumentException("기간 정보는 필수입니다.");
    }
    if (!is2HourBeforeStartAt(startAt)) {
      throw new IllegalArgumentException("시작일은 현재로부터 2시간 이후부터 가능합니다.");
    }
    if (!startAt.isBefore(endAt)) {
      throw new IllegalArgumentException("시작일이 종료일보다 나중일 수 없습니다.");
    }
    if (validDays != null && validDays < 1) {
      throw new IllegalArgumentException("유효일 기간이 1일 보다 작을 수 없습니다.");
    }
  }

  private boolean is2HourBeforeStartAt(LocalDateTime startAt) {
    LocalDateTime now = LocalDateTime.now();
    return now.isBefore(startAt.minusHours(2));
  }

  public boolean isValidIssuePeriod() {
    LocalDateTime now = LocalDateTime.now();
    return startAt.isBefore(now) && endAt.isAfter(now);
  }
}
