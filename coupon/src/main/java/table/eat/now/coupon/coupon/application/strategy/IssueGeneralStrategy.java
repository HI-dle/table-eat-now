package table.eat.now.coupon.coupon.application.strategy;

import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;

@Component
public class IssueGeneralStrategy implements IssueStrategy {

  @Override
  public void requestIssue(String couponUuid, Long userId) {
  }

  @Override
  public CouponProfile couponProfile() {
    return CouponProfile.GENERAL_BASE;
  }
}
