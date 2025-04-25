package table.eat.now.review.application.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.review.application.executor.TaskExecutorFactory;
import table.eat.now.review.application.executor.task.TaskExecutor;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

@ExtendWith(MockitoExtension.class)
class ReviewSchedulerTest {

  @Mock
  private UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;

  @Mock
  private TaskExecutorFactory executorFactory;

  @Mock
  private TaskExecutor taskExecutor;

  @InjectMocks
  private ReviewScheduler reviewScheduler;

  @BeforeEach
  void setup() {
    when(executorFactory.createSchedulerExecutor(any(), any())).thenReturn(taskExecutor);
  }

  @Nested
  class updateRestaurantRecentRatings_는 {

    @Test
    void TaskExecutor를_통해_실행된다() {
      // given
      when(executorFactory.createSchedulerExecutor(any(), any())).thenReturn(taskExecutor);

      // when
      reviewScheduler.updateRestaurantRecentRatings();

      // then
      verify(executorFactory).createSchedulerExecutor(any(), any());
      verify(taskExecutor).execute(any());
    }

    @Test
    void 전달된_Runnable이_실제로_usecase를_호출한다() {
      // given
      // 람다에서 지역 변수는 final 또는 effectively final 이어야 하기때문에 AtomicBoolean
      AtomicBoolean executed = new AtomicBoolean(false);
      when(executorFactory.createSchedulerExecutor(any(), any())).thenReturn(taskExecutor);
      doAnswer(invocation -> {
        // 첫 번째 인자 (task)
        Runnable runnable = invocation.getArgument(0);
        // task.execute() 호출
        runnable.run();
        // 실행 확인을 위한 플래그 (실행시 true)
        executed.set(true);
        // execute 의 return 값 (void)
        return null;
      }).when(taskExecutor).execute(any());

      // when
      reviewScheduler.updateRestaurantRecentRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute(any(LocalDateTime.class),
          any(LocalDateTime.class));
      assert executed.get();
    }

    @Test
    void 예외가_발생해도_스케줄러는_중단되지_않는다() {
      // given
      when(executorFactory.createSchedulerExecutor(any(), any())).thenReturn(taskExecutor);
      doAnswer(invocation -> {
        Runnable runnable = invocation.getArgument(0);
        runnable.run();
        return null;
      }).when(taskExecutor).execute(any());

      doThrow(new RuntimeException("에러")).when(updateRestaurantRatingUseCase)
          .execute(any(LocalDateTime.class), any(LocalDateTime.class));

      // when & then
      assertDoesNotThrow(() -> reviewScheduler.updateRestaurantRecentRatings());
      verify(taskExecutor).execute(any());
      verify(updateRestaurantRatingUseCase).execute(any(), any());
    }
  }

  @Nested
  class updateRestaurantDailyRatings_는 {

    @Test
    void TaskExecutor를_통해_실행된다() {
      // when
      reviewScheduler.updateRestaurantDailyRatings();

      // then
      verify(executorFactory).createSchedulerExecutor(any(), any());
      verify(taskExecutor).execute(any());
    }

    @Test
    void 전달된_Runnable이_실제로_usecase를_호출한다() {
      AtomicBoolean executed = new AtomicBoolean(false);
      doAnswer(invocation -> {
        Runnable runnable = invocation.getArgument(0);
        runnable.run();
        executed.set(true);
        return null;
      }).when(taskExecutor).execute(any());

      // when
      reviewScheduler.updateRestaurantDailyRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute(any(LocalDateTime.class), any(LocalDateTime.class));
      assert executed.get();
    }

    @Test
    void 예외가_발생해도_스케줄러는_중단되지_않는다() {
      doAnswer(invocation -> {
        Runnable runnable = invocation.getArgument(0);
        runnable.run();
        return null;
      }).when(taskExecutor).execute(any());

      doThrow(new RuntimeException("에러")).when(updateRestaurantRatingUseCase)
          .execute(any(LocalDateTime.class), any(LocalDateTime.class));

      // when & then
      assertDoesNotThrow(() -> reviewScheduler.updateRestaurantDailyRatings());
      verify(taskExecutor).execute(any());
      verify(updateRestaurantRatingUseCase).execute(any(), any());
    }
  }

}