package table.eat.now.coupon.coupon.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class AvailablePeriod {

  @Column(nullable = false)
  private LocalDateTime issueStartAt;

  @Column(nullable = false)
  private LocalDateTime issueEndAt;

  @Column
  private LocalDateTime expireAt;

  @Column
  private Integer validDays;

  private AvailablePeriod(LocalDateTime issueStartAt, LocalDateTime issueEndAt,
      LocalDateTime expireAt, Integer validDays, CouponLabel label) {

    validatePeriod(issueStartAt, issueEndAt, expireAt, validDays, label);

    this.issueStartAt = issueStartAt;
    this.issueEndAt = issueEndAt;
    this.expireAt = expireAt;
    this.validDays = validDays;
  }

  public static AvailablePeriod of(LocalDateTime issueStartAt, LocalDateTime issueEndAt,
      LocalDateTime expireAt, Integer validDays, CouponLabel label) {

    return new AvailablePeriod(issueStartAt, issueEndAt, expireAt, validDays, label);
  }

  public boolean is2HourBeforeIssueStartAt() {
    LocalDateTime now = LocalDateTime.now();
    return now.isBefore(issueStartAt.minusHours(2));
  }

  public boolean isAfterIssueEndAt() {
    LocalDateTime now = LocalDateTime.now();
    return now.isAfter(issueEndAt);
  }

  public boolean isValidIssuePeriod() {
    LocalDateTime now = LocalDateTime.now();
    return issueStartAt.isBefore(now) && issueEndAt.isAfter(now);
  }

  public LocalDateTime calcExpireAt() {
    if (expireAt != null) {
      return expireAt;
    }
    LocalDate today = LocalDate.now();
    return calcValidUntil(today);
  }

  public boolean isIssuableIn(LocalDate date) {
    LocalDateTime from = date.atStartOfDay();
    LocalDateTime to = from.plusDays(1);
    return issueStartAt.isBefore(to) && issueEndAt.isAfter(from);
  }

  private void validatePeriod(LocalDateTime issueStartAt, LocalDateTime issueEndAt,
      LocalDateTime expireAt, Integer validDays, CouponLabel label) {

    validateIssuePeriod(issueStartAt, issueEndAt);
    validateIssuePeriodByLabel(issueStartAt, issueEndAt, label);
    validateExpireAtByIssueEnd(issueEndAt, expireAt);
    validateExpiry(expireAt, validDays);
  }

  private boolean is2HourBeforeNewIssueStartAt(LocalDateTime issueStartAt) {
    LocalDateTime now = LocalDateTime.now();
    return now.isBefore(issueStartAt.minusHours(2));
  }

  private void validateIssuePeriod(LocalDateTime issueStartAt, LocalDateTime issueEndAt) {
    Assert.notNull(issueStartAt, "발급 시작일은 필수입니다.");
    Assert.notNull(issueEndAt, "발급 종료일은 필수입니다.");

    if (!is2HourBeforeNewIssueStartAt(issueStartAt)) {
      throw new IllegalArgumentException("시작일은 현재로부터 2시간 이후부터 가능합니다.");
    }
    if (issueStartAt.isAfter(issueEndAt)) {
      throw new IllegalArgumentException("시작일이 종료일보다 나중일 수 없습니다.");
    }
  }

  private void validateIssuePeriodByLabel(LocalDateTime issueStartAt, LocalDateTime issueEndAt, CouponLabel label) {
    if (label != CouponLabel.HOT) {
      return;
    }
    if (issueEndAt.isAfter(issueStartAt.plusHours(1))) {
      throw new IllegalArgumentException("핫딜 쿠폰은 발급 기간이 한시간을 초과할 수 없습니다.");
    }
  }

  private void validateExpireAtByIssueEnd(LocalDateTime issueEndAt, LocalDateTime expireAt) {
    if (expireAt != null && expireAt.isBefore(issueEndAt)) {
      throw new IllegalArgumentException("유효 기간이 종료일보다 이전일 수 없습니다.");
    }
  }

  private void validateExpiry(LocalDateTime expireAt, Integer validDays) {
    Assert.isTrue(expireAt != null || validDays != null, "만료일 또는 유효기간은 반드시 설정해야 합니다.");

    if (expireAt != null && expireAt.isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("유효 기간이 현재보다 이전일 수 없습니다.");
    }
    if (validDays != null && validDays < 1) {
      throw new IllegalArgumentException("유효일 기간이 1일 보다 작을 수 없습니다.");
    }
  }

  private boolean isNotExpired() {
    return expireAt.isBefore(LocalDateTime.now()) && issueStartAt.isAfter(LocalDateTime.now());
  }

  private boolean isValid() {
    return calcValidUntil(issueEndAt.toLocalDate()).isBefore(LocalDateTime.now())
        && issueStartAt.isAfter(LocalDateTime.now());
  }

  private LocalDateTime calcValidUntil(LocalDate date) {
    return date.plusDays(validDays + 1).atStartOfDay();
  }
}
