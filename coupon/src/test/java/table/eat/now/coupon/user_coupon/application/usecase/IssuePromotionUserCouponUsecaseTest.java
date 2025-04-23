package table.eat.now.coupon.user_coupon.application.usecase;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import table.eat.now.coupon.helper.IntegrationTestSupport;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.entity.UserCouponStatus;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;
import table.eat.now.coupon.user_coupon.domain.store.UserCouponStore;

class IssuePromotionUserCouponUsecaseTest extends IntegrationTestSupport {

  @Autowired
  IssuePromotionUserCouponUsecase usecase;

  @Autowired
  UserCouponStore store;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  UserCouponRepository repository;

  private List<IssueUserCouponCommand> commands;

  @BeforeEach
  void setUp() {

    jdbcTemplate.execute("ALTER TABLE p_user_coupon ALTER COLUMN id SET DEFAULT NEXT VALUE FOR p_user_coupon_seq");

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

  @DisplayName("프로모션 쿠폰 발행 배치 처리::jdbc 템플릿 사용 - 성공")
  @Test
  void execute() {
    // given
    // when
    long start = System.nanoTime();
    usecase.execute(commands);
    long end = System.nanoTime();
    System.out.println("수행 시간(ms): " + (end - start) / 1_000_000);

    // then
    UserCoupon userCoupon = repository.findByUserCouponUuidAndDeletedAtIsNull(
            commands.get(0).userCouponUuid())
        .orElseThrow(RuntimeException::new);

    assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
    assertThat(userCoupon.getExpiresAt()).isEqualTo(commands.get(0).expiresAt());
  }

  @DisplayName("프로모션 쿠폰 발행 배치 처리::엔티티 매니저 사용 - 성공")
  @Test
  void execute2() {
    // given
    // when
    long start = System.nanoTime();
    usecase.execute2(commands);
    long end = System.nanoTime();
    System.out.println("수행 시간(ms): " + (end - start) / 1_000_000);

    // then
    UserCoupon userCoupon = repository.findByUserCouponUuidAndDeletedAtIsNull(
            commands.get(0).userCouponUuid())
        .orElseThrow(RuntimeException::new);

    assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
    assertThat(userCoupon.getExpiresAt()).isEqualTo(commands.get(0).expiresAt());
  }
}