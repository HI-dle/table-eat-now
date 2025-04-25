package table.eat.now.coupon.coupon.application.service.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;

@Component
public class IssueStrategyResolver {

  private final Map<IssueStrategyAlias, IssueStrategy> strategyMap;

  public IssueStrategyResolver(List<IssueStrategy> strategies) {
    this.strategyMap = strategies.stream()
        .collect(Collectors.toMap(
            IssueStrategy::alias,
            Function.identity())
        );
  }

  public IssueStrategy resolve(IssueStrategyKey key) {
    IssueStrategyAlias strategyAlias = mapToStrategyAlias(key);
    return strategyMap.get(strategyAlias);
  }

  private IssueStrategyAlias mapToStrategyAlias(IssueStrategyKey key) {
    if (key.isLimited() && !key.allowDuplicated()) {
      return IssueStrategyAlias.HOT_LIMITED_NONDUP;
    }
    if (key.isLimited() && key.allowDuplicated()) {
      return IssueStrategyAlias.HOT_LIMITED;
    }
    if (!key.isLimited() && !key.allowDuplicated()) {
      return IssueStrategyAlias.HOT_NONDUP;
    }
    if (!key.isLimited() && key.allowDuplicated()) {
      return IssueStrategyAlias.GENERAL_BASE;
    }
    throw CustomException.from(CouponErrorCode.NON_EXIST_STRATEGY);
  }
}
