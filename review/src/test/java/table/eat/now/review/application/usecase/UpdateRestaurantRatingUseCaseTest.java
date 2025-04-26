package table.eat.now.review.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.review.application.batch.Cursor;
import table.eat.now.review.application.batch.CursorKey;
import table.eat.now.review.application.batch.CursorStore;
import table.eat.now.review.application.event.ReviewEventPublisher;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewContent;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.entity.ReviewVisibility;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.ReviewRepository;
import table.eat.now.review.helper.IntegrationTestSupport;

@TestPropertySource(properties = {
    "review.rating.update.batch-size=3"
})
class UpdateRestaurantRatingUseCaseTest extends IntegrationTestSupport {

  @Autowired
  private UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private CursorStore cursorStore;

  @MockitoBean
  private ReviewEventPublisher reviewEventPublisher;

  private final CursorKey cursorKey = CursorKey.RATING_UPDATE_RECENT_CURSOR;
  private final Duration interval = Duration.ofMinutes(5L);

  private LocalDateTime baseTime;

  @BeforeEach
  void setUp() {
    baseTime = LocalDateTime.now().minusMinutes(5);
  }

  @Test
  void 업데이트할_식당이_없으면_아무것도_처리하지_않는다() {
    // when
    updateRestaurantRatingUseCase.execute(cursorKey, interval);

    // then
    Cursor cursor = cursorStore.getCursor(cursorKey.value());
    assertThat(cursor.lastProcessedUpdatedAt()).isNull();
    assertThat(cursor.lastProcessedRestaurantId()).isNull();
  }

  @Test
  void 업데이트할_식당이_있으면_커서가_이동한다() {
    // given
    Review review1 = createReview("restaurant-1", baseTime.plusMinutes(1), 5);
    Review review2 = createReview("restaurant-2", baseTime.plusMinutes(2), 4);
    Review review3 = createReview("restaurant-3", baseTime.plusMinutes(3), 3);
    Review review4 = createReview("restaurant-4", baseTime.plusMinutes(4), 2);

    reviewRepository.saveAllAndFlush(List.of(review1, review2, review3, review4));
    // when
    updateRestaurantRatingUseCase.execute(cursorKey, interval);

    // then
    Cursor updatedCursor = cursorStore.getCursor(cursorKey.value());
    assertThat(updatedCursor.lastProcessedUpdatedAt()).isNotNull();
    assertThat(updatedCursor.lastProcessedRestaurantId()).isNotNull();

    assertThat(updatedCursor.lastProcessedRestaurantId()).isEqualTo("restaurant-4");
    verify(reviewEventPublisher, times(4)).publish(any());
  }

  @Test
  void 이벤트_발행_중_에러가_발생해도_나머지_식당은_계속_처리한다() {
    // given
    Review review1 = createReview("restaurant-1", baseTime.plusMinutes(1), 5);
    Review review2 = createReview("restaurant-2", baseTime.plusMinutes(2), 4);

    reviewRepository.saveAllAndFlush(List.of(review1, review2));
    // when
    updateRestaurantRatingUseCase.execute(cursorKey, interval);
    doThrow(new RuntimeException("테스트용 예외"))
        .when(reviewEventPublisher).publish(any());

    // then
    Cursor updatedCursor = cursorStore.getCursor(cursorKey.value());

    assertThat(updatedCursor.lastProcessedRestaurantId()).isEqualTo("restaurant-2");
  }

  @Test
  void 배치_조회_결과가_없으면_즉시_종료한다() {
    // given
    Review review = createReview("restaurant-1", baseTime.plusMinutes(1), 5);
    reviewRepository.save(review);

    updateRestaurantRatingUseCase.execute(cursorKey, interval);

    // 두 번째 실행 - 이미 처리된 이후라서 조회 결과 없음
    updateRestaurantRatingUseCase.execute(cursorKey, interval);

    // then
    Cursor updatedCursor = cursorStore.getCursor(cursorKey.value());
    assertThat(updatedCursor.lastProcessedRestaurantId()).isEqualTo("restaurant-1");
  }

  @Test
  void 같은_레스토랑ID가_여러번_나와도_마지막만_기준으로_커서가_이동한다() {
    // given
    Review review1 = createReview("restaurant-1", baseTime.plusMinutes(1), 5);
    Review review2 = createReview("restaurant-1", baseTime.plusMinutes(2), 4);

    reviewRepository.saveAllAndFlush(List.of(review1, review2));

    // when
    updateRestaurantRatingUseCase.execute(cursorKey, interval);

    // then
    Cursor updatedCursor = cursorStore.getCursor(cursorKey.value());
    assertThat(updatedCursor.lastProcessedUpdatedAt()).isEqualTo(review2.getUpdatedAt());
    assertThat(updatedCursor.lastProcessedRestaurantId()).isEqualTo("restaurant-1");
  }

  private Review createReview(String restaurantId, LocalDateTime updatedAt, int rating) {
    try {
      // Review 생성자 호출
      Constructor<Review> reviewConstructor = Review.class.getDeclaredConstructor();
      reviewConstructor.setAccessible(true);
      Review review = reviewConstructor.newInstance();

      // ReviewReference 생성자 호출
      Constructor<ReviewReference> referenceConstructor =
          ReviewReference.class.getDeclaredConstructor();
      referenceConstructor.setAccessible(true);
      ReviewReference reference = referenceConstructor.newInstance();

      // ReviewContent 생성자 호출
      Constructor<ReviewContent> contentConstructor = ReviewContent.class.getDeclaredConstructor();
      contentConstructor.setAccessible(true);
      ReviewContent content = contentConstructor.newInstance();

      // ReviewVisibility 생성자 호출
      Constructor<ReviewVisibility> visibilityConstructor =
          ReviewVisibility.class.getDeclaredConstructor();
      visibilityConstructor.setAccessible(true);
      ReviewVisibility visibility = visibilityConstructor.newInstance();

      // 필드 세팅 (ReflectionTestUtils 활용)
      ReflectionTestUtils.setField(reference, "restaurantId", restaurantId);
      ReflectionTestUtils.setField(reference, "serviceId", "test-service-id");
      ReflectionTestUtils.setField(reference, "customerId", 1L);
      ReflectionTestUtils.setField(reference, "serviceType", ServiceType.WAITING);

      ReflectionTestUtils.setField(content, "content", "리뷰 내용");
      ReflectionTestUtils.setField(content, "rating", rating);

      ReflectionTestUtils.setField(visibility, "isVisible", true);
      ReflectionTestUtils.setField(review, "reviewId", UUID.randomUUID().toString());
      ReflectionTestUtils.setField(review, "reference", reference);
      ReflectionTestUtils.setField(review, "content", content);
      ReflectionTestUtils.setField(review, "visibility", visibility);
      ReflectionTestUtils.setField(review, "updatedAt", updatedAt);

      return review;
    } catch (Exception e) {
      throw new RuntimeException("리뷰 생성 실패", e);
    }
  }
}
