package table.eat.now.coupon.coupon.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@RequiredArgsConstructor
@Component
public class NoDuplicateStrategy implements CouponIssueStrategy {
  private final CouponRepository couponRepository;

  @Override
  public boolean support(Coupon coupon) {
    return !coupon.getAllowDuplicate() && !coupon.hasStockCount();
  }

  @Override
  public void issue(String couponUuid, Long userId) {
    markAsIssued(couponUuid, userId);
  }

  private void markAsIssued(String couponUuid, Long userId) {
    boolean result = couponRepository.markAsIssued(couponUuid, userId);
    if (!result) {
      throw CustomException.from(CouponErrorCode.ALREADY_ISSUED);
    }
  }
}
