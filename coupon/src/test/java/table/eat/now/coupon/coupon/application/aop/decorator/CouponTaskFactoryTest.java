package table.eat.now.coupon.coupon.application.aop.decorator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.coupon.coupon.application.aop.annotation.WithSimpleTransaction;
import table.eat.now.coupon.coupon.application.aop.dto.LockTime;
import table.eat.now.coupon.helper.IntegrationTestSupport;

class CouponTaskFactoryTest extends IntegrationTestSupport {

  @Autowired
  private CouponTaskFactory couponTaskFactory;

  @DisplayName("주어진 첨가요소에 따라 데코레이트 된 Task 구현체 생성 및 위임 객체 검증 - 성공")
  @Test
  void createDecoratedTask() {
    // given
    WithSimpleTransaction withSimpleTransaction = mock(WithSimpleTransaction.class);
    when(withSimpleTransaction.readOnly()).thenReturn(true);

    CouponTaskCondiment condiment = CouponTaskCondiment.of(
        List.of("key"),
        LockTime.builder()
            .leaseTime(5)
            .waitTime(1)
            .timeUnit(TimeUnit.SECONDS)
            .build(),
        withSimpleTransaction
        );

    // when
    CouponTask decoratedCouponTask = couponTaskFactory.createDecoratedTask(condiment);

    // then
    Object delegate = ReflectionTestUtils.getField(decoratedCouponTask,"delegate");
    assertThat(decoratedCouponTask).isInstanceOf(CouponLockDecorator.class);
    assertThat(delegate).isInstanceOf(CouponTransactionReadOnlyDecorator.class);
  }
}