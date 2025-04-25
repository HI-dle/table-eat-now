package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class IssueStrategyResolverTest extends IntegrationTestSupport {

  @Autowired
  IssueStrategyResolver resolver;

  public static Stream<Arguments> getIssueStrategyKeys() {
    return Stream.of(
        Arguments.of(IssueStrategyKey.of("HOT", false, true)),
        Arguments.of(IssueStrategyKey.of("HOT", true, true)),
        Arguments.of(IssueStrategyKey.of("HOT", false, false)),
        Arguments.of(IssueStrategyKey.of("HOT", true, false))
    );
  }

  @MethodSource("getIssueStrategyKeys")
  @ParameterizedTest(name = "전략 리졸버를 통해서 전략 조회 검증 - {0} 키는 전략을 조회할 수 있다.")
  void resolve(IssueStrategyKey keyParam) {
    // given
    IssueStrategyKey key = keyParam;

    // when
    IssueStrategy generalStrategy = resolver.resolve(key);

    // then
    assertThat(generalStrategy).isInstanceOf(IssueStrategy.class);
  }

  @DisplayName("전략 리졸버를 통해서 전략 조회 검증 - 주어진 키에 따라 일반 발급 전략 확인 성공")
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