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
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.helper.IntegrationTestSupport;

@Slf4j
class NoDuplicateWithStockStrategyTest extends IntegrationTestSupport {

  @Autowired
  private NoDuplicateWithStockStrategy noDuplicateWithStockStrategy;

  @Autowired
  private CouponRepository couponRepository;

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = CouponFixture.createCoupon(
        1, "FIXED_DISCOUNT", 2, false, 2000, null, null);
    ReflectionTestUtils.setField(coupon.getPeriod(), "startAt", LocalDateTime.now().minusDays(1));
    couponRepository.save(coupon);

    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getEndAt())
        .plusMinutes(10);
    couponRepository.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    couponRepository.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 중복 발급 제한 및 수량 제한 쿠폰 발급 전략이 지원하는지 확인 - 성공")
  @Test
  void support() {
    // given, when
    boolean support = noDuplicateWithStockStrategy.support(coupon);

    // then
    assertThat(support).isTrue();
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰 발급 확인 - 발급된 유저 목록에 포함되어 있는지 확인")
  @Test
  void issue() {
    // given
    noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThat(couponRepository.isAlreadyIssued(coupon.getCouponUuid(), 2L)).isTrue();
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 중복 없이 발급하는지 확인 - 중복 발급 시도시 예외 발생")
  @Test
  void issueFailedWhenDuplicated() {
    // given
    noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 2L);

    // when, then
    assertThatThrownBy(() -> noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 2L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.ALREADY_ISSUED.getMessage());
  }

  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 수량 제한해서 발급하는지 확인 - 수량 초과시 예외 발생")
  @Test
  void issueFailedWhenSoldOut() {
    // given
    noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 2L);
    noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 3L);

    // when, then
    assertThatThrownBy(() -> noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 4L))
        .isInstanceOf(CustomException.class)
        .hasMessage(CouponErrorCode.INSUFFICIENT_STOCK.getMessage());
  }


  @DisplayName("중복 발급 제한 및 수량 제한 쿠폰을 중복 없이 발급하는지 확인 - 동시성 문제 발생시 롤백 발생")
  @Test
  void issueFailedThenRollback() throws InterruptedException {
    // given
    int threadCount = 2;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(threadCount);

    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          noDuplicateWithStockStrategy.issue(coupon.getCouponUuid(), 2L);
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
    assertThat(couponRepository.isAlreadyIssued(coupon.getCouponUuid(), 2L))
        .isTrue();
    assertThat(couponRepository.getCouponCount(coupon.getCouponUuid()))
        .isEqualTo(coupon.getCount()-1);
  }
}