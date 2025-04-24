package table.eat.now.coupon.user_coupon.application.usecase;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.entity.UserCouponStatus;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;
import table.eat.now.coupon.user_coupon.domain.store.UserCouponStore;

@Slf4j
class IssuePromotionUserCouponUsecaseTest extends IntegrationTestSupport {

  @Autowired
  IssuePromotionUserCouponUsecase usecase;

  @Autowired
  UserCouponStore store;

  @Autowired
  UserCouponRepository repository;

  private List<IssueUserCouponCommand> commands;

  @BeforeEach
  void setUp() {

    String couponUuid = UUID.randomUUID().toString();
    commands = IntStream.range(0, 1000)
        .mapToObj(i -> IssueUserCouponCommand.builder()
            .couponUuid(couponUuid)
            .userCouponUuid(UUID.randomUUID().toString())
            .userId(i + 10L)
            .name("5월 정기할인쿠폰")
            .expiresAt(LocalDateTime.of(2025, 5, 31, 0, 0))
            .build())
        .toList();
  }

  public static Stream<IssueMethod> methodProvider() {
    return Stream.of(IssueMethod.JDBC, IssueMethod.JPA);
  }

  @DisplayName("프로모션 쿠폰 발행 배치 처리 - 성공")
  @MethodSource("methodProvider")
  @ParameterizedTest(name = "{index} : 수행 방식 - {0}")
  void execute(IssueMethod method) {
    // given
    // when
    long start = System.nanoTime();
    method.executeWith(usecase, commands);
    long end = System.nanoTime();
    log.info("수행 시간(ms): {}", (end - start) / 1_000_000);

    // then
    UserCoupon userCoupon = repository.findByUserCouponUuidAndDeletedAtIsNull(
            commands.get(0).userCouponUuid())
        .orElseThrow(RuntimeException::new);

    assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
    assertThat(userCoupon.getExpiresAt()).isEqualTo(commands.get(0).expiresAt());
  }

  @Getter
  @RequiredArgsConstructor
  enum IssueMethod {
    JDBC("JdbcTemplate 활용",
        (usecase,  commands) -> usecase.execute(commands)),
    JPA("EntityManager 활용",
        (usecase,  commands) -> usecase.execute2(commands));

    final String description;
    final BiConsumer<IssuePromotionUserCouponUsecase, List<IssueUserCouponCommand>> consumer;

    public void executeWith(IssuePromotionUserCouponUsecase usecase, List<IssueUserCouponCommand> commands) {
      consumer.accept(usecase, commands);
    }

    @Override
    public String toString() {
      return description;
    }
  }
}