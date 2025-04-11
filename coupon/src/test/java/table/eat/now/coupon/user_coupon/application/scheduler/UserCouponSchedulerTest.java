package table.eat.now.coupon.user_coupon.application.scheduler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import table.eat.now.coupon.user_coupon.application.usecase.ReleaseUserCouponUsecase;

@ExtendWith(SpringExtension.class)
@Import(UserCouponScheduler.class)
class UserCouponSchedulerTest {

  @Autowired
  private UserCouponScheduler userCouponScheduler;

  @MockitoBean
  private ReleaseUserCouponUsecase releaseUserCouponUsecase;


  @DisplayName("스케쥴러 호출시 usecase가 잘 동작하는지 확인")
  @Test
  void releaseUserCoupon() {
    // given
    doNothing().when(releaseUserCouponUsecase).execute();

    // when
    userCouponScheduler.releaseUserCoupon();

    // then
    verify(releaseUserCouponUsecase, times(1)).execute();
  }
}