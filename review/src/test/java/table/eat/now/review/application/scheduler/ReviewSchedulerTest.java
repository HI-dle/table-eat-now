package table.eat.now.review.application.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

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
      // when
      reviewScheduler.updateRestaurantRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute();
    }

    @Test
    void 예외가_발생해도_정상적으로_동작한다() {
      // given
      doThrow(new RuntimeException("Test exception"))
          .when(updateRestaurantRatingUseCase).execute();

      // when
      reviewScheduler.updateRestaurantRatings();

      // then
      verify(updateRestaurantRatingUseCase, times(1)).execute();
      assertDoesNotThrow(() -> reviewScheduler.updateRestaurantRatings());
    }
  }
}