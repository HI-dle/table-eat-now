package table.eat.now.coupon.coupon.application.service.strategy;

public interface IssueStrategy {
  void requestIssue(String couponUuid, Long userId);
  IssueStrategyAlias alias();
}
