package table.eat.now.coupon.coupon.application.scheduler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import table.eat.now.coupon.coupon.application.usecase.PrepareCouponIssuanceUsecase;

@ExtendWith(SpringExtension.class)
@Import(CouponScheduler.class)
class CouponSchedulerTest {

  @Autowired
  private CouponScheduler couponScheduler;
  @MockitoBean
  private PrepareCouponIssuanceUsecase prepareCouponIssuanceUsecase;

  @BeforeEach
  void setUp() {
  }

  @DisplayName("스케쥴러 호출시 usecase가 잘 동작하는지 확인")
  @Test
  void setCouponIssuanceInfo() {
    // given
    doNothing().when(prepareCouponIssuanceUsecase).execute();

    // when
    couponScheduler.setCouponIssuanceInfo();

    // then
    verify(prepareCouponIssuanceUsecase, times(1)).execute();
  }
}