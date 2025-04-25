package table.eat.now.coupon.coupon.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@RequiredArgsConstructor
@Component
public class IssueLimitedHotStrategy implements IssueStrategy {
  private final CouponRepository couponRepository;

  @Override
  public void requestIssue(String couponUuid, Long userId) {
    checkStockAndDecrease(couponUuid);
  }

  @Override
  public IssueStrategyAlias alias() {
    return IssueStrategyAlias.HOT_LIMITED;
  }

  private void checkStockAndDecrease(String couponUuid) {
    Long remainder = couponRepository.decreaseCouponCount(couponUuid);
    boolean result = remainder !=null && remainder >= 0;
    if (!result) {
      throw CustomException.from(CouponErrorCode.INSUFFICIENT_STOCK);
    }
  }
}

