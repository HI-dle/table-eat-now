package table.eat.now.coupon.coupon.application.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class IssueGeneralStrategy implements IssueStrategy {

  @Override
  public void requestIssue(String couponUuid, Long userId) {
  }

  @Override
  public IssueStrategyAlias alias() {
    return IssueStrategyAlias.GENERAL_BASE;
  }
}
