package table.eat.now.coupon.coupon.application.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.application.usecase.PrepareCouponIssuanceUsecase;
import table.eat.now.coupon.coupon.application.usecase.PrepareDailyCouponCacheUsecase;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class CouponSchedulerTest extends IntegrationTestSupport {

  @Autowired
  private CouponScheduler couponScheduler;

  @MockitoBean
  private PrepareCouponIssuanceUsecase prepareCouponIssuanceUsecase;

  @MockitoBean
  private PrepareDailyCouponCacheUsecase prepareDailyCouponCacheUsecase;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("쿠폰 발행 준비 스케쥴러 메소드 호출시 내부 로직이 잘 동작하는지 확인 - 성공")
  @Test
  void setCouponIssuanceInfo() {
    // given
    doNothing().when(prepareCouponIssuanceUsecase).execute();

    // when
    couponScheduler.setCouponIssuanceInfo();

    // then
    verify(prepareCouponIssuanceUsecase, times(1)).execute();
  }

  @DisplayName("쿠폰 발행 준비 스케쥴러 메소드 분산락 확인: 하나만 실행되고, 나머지는 락 획득 실패 예외 발생 - 성공")
  @Test
  void setCouponIssuanceInfoWithLock() {
    // given
    int numberOfThreads = 3;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    // when
    List<Future<?>> futures = List.of(
        executorService.submit(() -> couponScheduler.setCouponIssuanceInfo()),
        executorService.submit(() -> couponScheduler.setCouponIssuanceInfo()),
        executorService.submit(() -> couponScheduler.setCouponIssuanceInfo())
    );

    List<Exception> results = futures.stream()
        .map(super::extractExceptionFromFuture)
        .toList();

    executorService.shutdown();

    // then
    List<Exception> list = results.stream()
        .filter(exception -> exception instanceof CustomException)
        .toList();
    long failed =  list.size();

    assertThat(failed).isEqualTo(2);
    assertThat(list.get(0)).isInstanceOf(CustomException.class).hasMessage(CouponErrorCode.LOCK_PROBLEM.getMessage());
  }

  @DisplayName("쿠폰 캐시 준비 스케쥴러 메소드 호출시 내부 로직이 잘 동작하는지 확인 - 성공")
  @Test
  void prepareDailyCouponCache() {
    // given
    doNothing().when(prepareDailyCouponCacheUsecase).execute();

    // when
    couponScheduler.prepareDailyCouponCache();

    // then
    verify(prepareDailyCouponCacheUsecase, times(1)).execute();
  }
}