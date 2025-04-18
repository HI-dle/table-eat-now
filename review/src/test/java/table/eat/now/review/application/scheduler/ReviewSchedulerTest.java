package table.eat.now.review.application.scheduler;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewSchedulerTest {

  @Mock
  private UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;

  @InjectMocks
  private ReviewScheduler reviewScheduler;

  @Nested
  class updateRestaurantRatings_는 {

    @Test
    void execute_메서드를_호출할_수_있다() {
      // given
      int batchSize = 100;
      ReflectionTestUtils.setField(reviewScheduler, "batchSize", batchSize);

      // when
      reviewScheduler.updateRestaurantRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute(batchSize);
    }

    @Test
    void 예외가_발생해도_정상적으로_동작한다() {
      // given
      int batchSize = 100;
      ReflectionTestUtils.setField(reviewScheduler, "batchSize", batchSize);

      doThrow(new RuntimeException("Test exception"))
          .when(updateRestaurantRatingUseCase).execute(batchSize);

      // when
      reviewScheduler.updateRestaurantRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute(batchSize);
    }
  }
}