package table.eat.now.coupon.coupon.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@RequiredArgsConstructor
@Component
public class WithStockStrategy  implements CouponIssueStrategy {
  private final CouponRepository couponRepository;

  @Override
  public boolean support(Coupon coupon) {
    return coupon.getAllowDuplicate() && coupon.hasStockCount();
  }

  @Override
  public void issue(String couponUuid, Long userId) {
    checkStockAndDecrease(couponUuid);
  }

  private void checkStockAndDecrease(String couponUuid) {
    boolean result = couponRepository.decreaseCouponCount(couponUuid);
    if (!result) {
      throw CustomException.from(CouponErrorCode.INSUFFICIENT_STOCK);
    }
  }
}

