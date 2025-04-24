package table.eat.now.coupon.coupon.application.service.strategy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public record IssueStrategyKey(
    CouponLabel label,
    boolean isLimited,
    boolean allowDuplicated
) {

  public static IssueStrategyKey of(String label, boolean isLimited, boolean allowDuplicated) {

    return new IssueStrategyKey(CouponLabel.parse(label), isLimited, allowDuplicated);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum CouponLabel {
    HOT, GENERAL, SYSTEM,
    ;

    static CouponLabel parse(String label) {
      return CouponLabel.valueOf(label.toUpperCase());
    }
  }
}