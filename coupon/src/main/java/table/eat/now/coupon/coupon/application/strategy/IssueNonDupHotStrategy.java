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
public class IssueNonDupHotStrategy implements IssueStrategy {
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  @Override
  public CouponProfile couponProfile() {
    return CouponProfile.HOT_NONDUP;
  }

  @Override
  public void requestIssue(String couponUuid, Long userId) {
    markAsIssued(couponUuid, userId);
  }

  private void markAsIssued(String couponUuid, Long userId) {
    boolean result = couponStore.markAsIssued(couponUuid, userId);
    if (!result) {
      throw CustomException.from(CouponErrorCode.ALREADY_ISSUED);
    }
  }
}
