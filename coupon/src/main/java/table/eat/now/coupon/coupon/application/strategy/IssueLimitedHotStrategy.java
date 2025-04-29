package table.eat.now.coupon.coupon.application.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@RequiredArgsConstructor
@Component
public class IssueLimitedHotStrategy implements IssueStrategy {
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  @Override
  public void requestIssue(String couponUuid, Long userId) {
    checkStockAndDecrease(couponUuid);
  }

  @Override
  public CouponProfile couponProfile() {
    return CouponProfile.HOT_LIMITED;
  }

  private void checkStockAndDecrease(String couponUuid) {
    Long remainder = couponStore.decreaseCouponCount(couponUuid);
    boolean result = remainder !=null && remainder >= 0;
    if (!result) {
      throw CustomException.from(CouponErrorCode.INSUFFICIENT_STOCK);
    }
  }
}

