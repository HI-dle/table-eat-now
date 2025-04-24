package table.eat.now.review.application.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.review.application.helper.LockExecutor;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

@ExtendWith(MockitoExtension.class)
class ReviewSchedulerTest {

  @Mock
  private UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;

  @Mock
  private LockExecutor lockExecutor;

  @InjectMocks
  private ReviewScheduler reviewScheduler;

  @Nested
  class updateRestaurantRatings_는 {

    @Test
    void LockExecutor를_통해_락이_실행된다() {
      // when
      reviewScheduler.updateRestaurantRatings();

      // then
      verify(lockExecutor, times(1)).execute(any(), any());
    }

    @Test
    void 전달된_Runnable이_실제로_usecase를_호출한다() {
      // given
      // 람다에서 지역 변수는 final 또는 effectively final 이어야 하기때문에 AtomicBoolean
      AtomicBoolean taskExecuted = new AtomicBoolean(false);

      doAnswer(invocation -> {
        // 두 번째 인자 (task)
        Runnable runnable = invocation.getArgument(1);
        // task.execute() 호출
        runnable.run();
        // 실행 확인을 위한 플래그 (실행시 true)
        taskExecuted.set(true);
        // execute 의 return 값
        return null;
      }).when(lockExecutor).execute(any(), any());

      // when
      reviewScheduler.updateRestaurantRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute();
      assert taskExecuted.get();
    }

    @Test
    void task_내부_예외가_발생해도_스케줄러는_중단되지_않는다() {
      // given
      doAnswer(invocation -> {
        Runnable task = invocation.getArgument(1);
        task.run();
        return null;
      }).when(lockExecutor).execute(any(), any());

      doThrow(new RuntimeException("UseCase 내부 예외"))
          .when(updateRestaurantRatingUseCase).execute();

      // when & then
      assertDoesNotThrow(() -> reviewScheduler.updateRestaurantRatings());
      verify(lockExecutor, times(1)).execute(any(), any());
      verify(updateRestaurantRatingUseCase, times(1)).execute();
    }
  }
}