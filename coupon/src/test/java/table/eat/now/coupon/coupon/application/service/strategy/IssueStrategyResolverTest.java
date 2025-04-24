package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class IssueStrategyResolverTest extends IntegrationTestSupport {

  @Autowired
  IssueStrategyResolver resolver;

  @Test
  void resolve() {
    // given
    IssueStrategyKey key = IssueStrategyKey.of("HOT", false, true);

    // when
    IssueStrategy generalStrategy = resolver.resolve(key);

    // then
    assertThat(generalStrategy.getClass()).isEqualTo(IssueGeneralStrategy.class);
  }
}