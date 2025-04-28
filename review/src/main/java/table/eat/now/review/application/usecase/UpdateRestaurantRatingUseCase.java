package table.eat.now.review.application.usecase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.review.application.batch.Cursor;
import table.eat.now.review.application.batch.CursorKey;
import table.eat.now.review.application.batch.CursorStore;
import table.eat.now.review.application.event.RestaurantRatingUpdateEvent;
import table.eat.now.review.application.event.RestaurantRatingUpdatePayload;
import table.eat.now.review.application.event.ReviewEventPublisher;
import table.eat.now.review.domain.repository.ReviewRepository;
import table.eat.now.review.domain.repository.search.CursorResult;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateRestaurantRatingUseCase {

  private final CursorStore cursorStore;
  private final ReviewRepository reviewRepository;
  private final ReviewEventPublisher reviewEventPublisher;

  @Value("${review.rating.update.batch-size:100}")
  private int batchSize;

  @Transactional(readOnly = true)
  public void execute(CursorKey cursorKey, Duration interval) {

    LocalDateTime endTime = LocalDateTime.now();
    Cursor cursor = cursorStore.getCursor(cursorKey.value());
    LocalDateTime startTime = cursor.lastProcessedUpdatedAt() != null
        ? cursor.lastProcessedUpdatedAt()
        : endTime.minus(interval);

    String lastRestaurantId = cursor.lastProcessedRestaurantId();

    Cursor startCursor = Cursor.of(startTime, lastRestaurantId);

    CursorResult endCursorResult = reviewRepository.findEndCursorResult(endTime);
    if (endCursorResult == null) {
      log.info("[{}] 업데이트할 레스토랑 평점이 없습니다.", cursorKey.name());
      return;
    }

    Cursor endCursor = Cursor.from(endCursorResult);

    if (startCursor.compareTo(endCursor) >= 0) {
      log.info("[{}] 처리할 범위가 없습니다. startCursor={}, endCursor={}",
          cursorKey.name(), startCursor, endCursor);
      return;
    }

    BatchProcessor.builder()
        .reviewRepository(reviewRepository)
        .reviewEventPublisher(reviewEventPublisher)
        .cursorStore(cursorStore)
        .cursorKey(cursorKey.value())
        .initialCursor(startCursor)
        .endCursor(endCursor)
        .batchSize(batchSize)
        .startTime(startTime)
        .endTime(endTime)
        .build().process();
  }

  @Builder
  private static class BatchProcessor {

    private final ReviewRepository reviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;
    private final CursorStore cursorStore;
    private final String cursorKey;
    private final Cursor initialCursor;
    private final Cursor endCursor;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int batchSize;

    private Cursor currentCursor;

    void process() {
      this.currentCursor = this.initialCursor;

      while (currentCursor.compareTo(endCursor) < 0) {
        List<CursorResult> batch = reviewRepository.findRecentlyUpdatedRestaurantIds(
            startTime, endTime,
            currentCursor.lastProcessedUpdatedAt(),
            currentCursor.lastProcessedRestaurantId(),
            batchSize
        );

        if (batch.isEmpty()) {
          break;
        }

        List<RestaurantRatingResult> results = reviewRepository
            .calculateRestaurantRatings(
                batch.stream()
                    .map(CursorResult::restaurantId)
                    .toList()
            );

        results.forEach(result -> {
          try {
            reviewEventPublisher.publish(
                RestaurantRatingUpdateEvent.of(
                    RestaurantRatingUpdatePayload.from(result)));
          } catch (Exception e) {
            log.error("레스토랑 평점 업데이트 이벤트 발행 실패", e);
          }
        });

        moveCursor(batch);

        log.info("레스토랑 평점 업데이트: {}개 처리 완료 (currentCursor: {})", batch.size(), currentCursor);
      }

      log.info("레스토랑 평점 업데이트 작업 완료");
    }

    private void moveCursor(List<CursorResult> batch) {
      CursorResult result = batch.get(batch.size() - 1);
      this.currentCursor = Cursor.from(result);
      cursorStore.saveCursor(cursorKey, currentCursor);
    }
  }
}