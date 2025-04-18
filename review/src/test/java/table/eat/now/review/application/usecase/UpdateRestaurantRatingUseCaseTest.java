package table.eat.now.review.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.review.application.event.RestaurantRatingUpdateEvent;
import table.eat.now.review.application.event.ReviewEventPublisher;
import table.eat.now.review.domain.repository.ReviewRepository;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "review.rating.update.batch-size=3"
})
class UpdateRestaurantRatingUseCaseTest {

  @MockitoBean
  private ReviewRepository reviewRepository;

  @MockitoBean
  private ReviewEventPublisher reviewEventPublisher;

  @Autowired
  private UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;

  @Nested
  class execute_메서드는 {

    @Test
    void 업데이트할_식당이_없으면_아무_처리도_하지_않는다() {
      // given
      when(reviewRepository.countRecentlyUpdatedRestaurants(any(LocalDateTime.class)))
          .thenReturn(0L);

      // when
      updateRestaurantRatingUseCase.execute();

      // then
      verify(reviewRepository, times(1))
          .countRecentlyUpdatedRestaurants(any(LocalDateTime.class));

      verify(reviewRepository, never())
          .findRecentlyUpdatedRestaurantIds(any(), anyLong(), anyInt());

      verify(reviewRepository, never())
          .calculateRestaurantRatings(anyList());

      verify(reviewEventPublisher, never())
          .publish(any());
    }

    @Test
    void 업데이트할_식당이_있으면_모든_배치를_처리한다() {
      // given
      int batchSize = 3;

      List<String> batch1 = Arrays.asList("1", "2", "3");
      List<String> batch2 = Arrays.asList("4", "5", "6");
      List<String> batch3 = Collections.singletonList("7");

      List<RestaurantRatingResult> result1 = List.of(
          new RestaurantRatingResult("1", new BigDecimal("4.5")),
          new RestaurantRatingResult("2", new BigDecimal("3.2")),
          new RestaurantRatingResult("3", new BigDecimal("4.0"))
      );

      List<RestaurantRatingResult> result2 = List.of(
          new RestaurantRatingResult("4", new BigDecimal("3.5")),
          new RestaurantRatingResult("5", new BigDecimal("2.8")),
          new RestaurantRatingResult("6", new BigDecimal("4.2"))
      );

      List<RestaurantRatingResult> result3 = List.of(
          new RestaurantRatingResult("7", new BigDecimal("5.0"))
      );

      // 스텁 설정
      when(reviewRepository.countRecentlyUpdatedRestaurants(any(LocalDateTime.class)))
          .thenReturn(7L);

      when(reviewRepository
          .findRecentlyUpdatedRestaurantIds(
              any(LocalDateTime.class), eq(0L), eq(batchSize)))
          .thenReturn(batch1);
      when(reviewRepository
          .findRecentlyUpdatedRestaurantIds(
              any(LocalDateTime.class), eq(3L), eq(batchSize)))
          .thenReturn(batch2);
      when(reviewRepository
          .findRecentlyUpdatedRestaurantIds(
              any(LocalDateTime.class), eq(6L), eq(batchSize)))
          .thenReturn(batch3);
      when(reviewRepository
          .findRecentlyUpdatedRestaurantIds(
              any(LocalDateTime.class), eq(7L), eq(batchSize)))
          .thenReturn(Collections.emptyList());

      when(reviewRepository.calculateRestaurantRatings(batch1)).thenReturn(result1);
      when(reviewRepository.calculateRestaurantRatings(batch2)).thenReturn(result2);
      when(reviewRepository.calculateRestaurantRatings(batch3)).thenReturn(result3);

      // when
      updateRestaurantRatingUseCase.execute();

      // then
      verify(reviewRepository)
          .countRecentlyUpdatedRestaurants(any(LocalDateTime.class));

      verify(reviewRepository, times(3))
          .findRecentlyUpdatedRestaurantIds(any(LocalDateTime.class), anyLong(), eq(batchSize));

      verify(reviewRepository, times(3))
          .calculateRestaurantRatings(anyList());

      verify(reviewEventPublisher, times(3))
          .publish(any(RestaurantRatingUpdateEvent.class));
    }

    @Test
    void 평점계산_중_에러가_발생해도_나머지_식당은_계속_처리한다() {
      // given
      int batchSize = 3;
      List<String> batch1 = Collections.singletonList("1");
      List<String> batch2 = Collections.singletonList("2");

      List<RestaurantRatingResult> result2 = Collections.singletonList(
          new RestaurantRatingResult("2", new BigDecimal("4.0"))
      );

      when(reviewRepository.countRecentlyUpdatedRestaurants(any(LocalDateTime.class)))
          .thenReturn(2L);

      when(reviewRepository.findRecentlyUpdatedRestaurantIds(any(LocalDateTime.class), eq(0L),
          eq(batchSize))).thenReturn(batch1);
      when(reviewRepository.findRecentlyUpdatedRestaurantIds(any(LocalDateTime.class), eq(1L),
          eq(batchSize))).thenReturn(batch2);
      when(reviewRepository.findRecentlyUpdatedRestaurantIds(any(LocalDateTime.class), eq(2L),
          eq(batchSize))).thenReturn(Collections.emptyList());

      when(reviewRepository.calculateRestaurantRatings(eq(batch1)))
          .thenThrow(new RuntimeException("테스트 예외"));
      when(reviewRepository.calculateRestaurantRatings(eq(batch2)))
          .thenReturn(result2);

      // when
      updateRestaurantRatingUseCase.execute();

      // then
      verify(reviewRepository, times(1))
          .countRecentlyUpdatedRestaurants(any(LocalDateTime.class));

      verify(reviewRepository, times(2))
          .findRecentlyUpdatedRestaurantIds(any(LocalDateTime.class), anyLong(), eq(batchSize));

      verify(reviewRepository, times(2))
          .calculateRestaurantRatings(anyList());

      verify(reviewEventPublisher, times(1))
          .publish(any(RestaurantRatingUpdateEvent.class));
    }
  }
}