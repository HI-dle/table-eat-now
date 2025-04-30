package table.eat.now.coupon.coupon.application.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;

@Component
public class IssueStrategyResolver {

  private final Map<CouponProfile, IssueStrategy> strategyMap;

  public IssueStrategyResolver(List<IssueStrategy> strategies) {
    this.strategyMap = strategies.stream()
        .collect(Collectors.toMap(
            IssueStrategy::couponProfile,
            Function.identity())
        );
  }

  public IssueStrategy resolve(Coupon coupon) {
    CouponProfile couponProfile = CouponProfile.parse(coupon);
    return strategyMap.get(couponProfile);
  }
}
