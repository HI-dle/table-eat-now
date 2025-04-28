package table.eat.now.coupon.coupon.application.service.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.application.strategy.IssueLimitedNonDupHotStrategy;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

@Slf4j
class IssueLimitedNonDupHotStrategyTest extends IntegrationTestSupport {

  @Autowired
  private IssueLimitedNonDupHotStrategy issueLimitedNonDupHotStrategy;

  @Autowired
  private CouponReader couponReader;

  @Autowired
  private CouponStore couponStore;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", "GENERAL", 2, false, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "issueStartAt", LocalDateTime.now().minusDays(1));
    couponStore.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getIssueEndAt())
        .plusMinutes(10);
    couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰 발급 전략이 발급 전략 별명을 잘 반환하는지 확인 - 성공")
  @Test
  void alias() {
    // given, when
    CouponProfile alias = issueLimitedNonDupHotStrategy.couponProfile();

    // then
    assertThat(alias).isEqualTo(CouponProfile.HOT_LIMITED_NONDUP);
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰 발급 확인 - 발급된 유저 목록에 포함되어 있는지 확인")
  @Test
  void issue() {
    // given
    issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThat(couponReader.isAlreadyIssued(coupon.getCouponUuid(), 2L)).isTrue();
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 중복 없이 발급하는지 확인 - 중복 발급 시도시 예외 발생")
  @Test
  void issueFailedWhenDuplicated() {
    // given
    issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThatThrownBy(() -> issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.ALREADY_ISSUED.getMessage());
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 수량 제한해서 발급하는지 확인 - 수량 초과시 예외 발생")
  @Test
  void issueFailedWhenSoldOut() {
    // given
    issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L);
    issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 3L);

    // when, then
    assertThatThrownBy(() -> issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 4L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.INSUFFICIENT_STOCK.getMessage());
  }


  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 중복 없이 발급하는지 확인 - 동시성 문제 발생시 롤백 발생")
  @Test
  void issueFailedThenRollback() throws InterruptedException {
    // given
    int threadCount = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          issueLimitedNonDupHotStrategy.requestIssue(coupon.getCouponUuid(), 2L);
        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();
    executorService.shutdown();

    // then
    assertThat(couponReader.isAlreadyIssued(coupon.getCouponUuid(), 2L))
        .isTrue();
    assertThat(couponReader.getCouponCount(coupon.getCouponUuid()))
        .isEqualTo(coupon.getCount()-1);
  }
}