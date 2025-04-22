package table.eat.now.review.application.usecase;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.review.application.event.RestaurantRatingUpdateEvent;
import table.eat.now.review.application.event.RestaurantRatingUpdatePayload;
import table.eat.now.review.application.event.ReviewEventPublisher;
import table.eat.now.review.domain.repository.ReviewRepository;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateRestaurantRatingUseCase {

  @Value("${review.rating.update.batch-size:100}")
  private int batchSize;
  @Value("${review.rating.update.recent-minutes:5}")
  private int recentMinutes;

  private final ReviewRepository reviewRepository;
  private final ReviewEventPublisher reviewEventPublisher;

  @Transactional(readOnly = true)
  public void execute() {
    LocalDateTime endTime = LocalDateTime.now();
    LocalDateTime startTime = endTime.minusMinutes(recentMinutes);

    long totalCount = reviewRepository.countRecentlyUpdatedRestaurants(startTime, endTime);

    if (totalCount == 0) {
      logNoRestaurantsToUpdate();
      return;
    }

    logStartBatchUpdate(totalCount);

    BatchProcessor.builder()
        .reviewRepository(reviewRepository)
        .reviewEventPublisher(reviewEventPublisher)
        .totalCount(totalCount)
        .batchSize(batchSize)
        .startTime(startTime)
        .endTime(endTime)
        .build().process();
  }

  private void logNoRestaurantsToUpdate() {
    log.info("업데이트할 레스토랑 평점이 없습니다.");
  }

  private void logStartBatchUpdate(long totalCount) {
    log.info("총 {}개 레스토랑의 평점을 배치 단위로 업데이트합니다.", totalCount);
  }

  private static class BatchProcessor {

    private final long totalCount;
    private final int batchSize;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Set<String> processedIds;
    private long processedCount;
    private final ReviewRepository reviewRepository;
    private final ReviewEventPublisher reviewEventPublisher;

    @Builder
    BatchProcessor(
        ReviewRepository reviewRepository,
        ReviewEventPublisher reviewEventPublisher,
        long totalCount,
        int batchSize,
        LocalDateTime startTime,
        LocalDateTime endTime
    ) {
      this.reviewRepository = reviewRepository;
      this.reviewEventPublisher = reviewEventPublisher;
      this.totalCount = totalCount;
      this.batchSize = batchSize;
      this.startTime = startTime;
      this.endTime = endTime;
      this.processedIds = new HashSet<>();
      this.processedCount = 0;
    }

    void process() {
      while (processedCount < totalCount) {
        List<String> batch = reviewRepository
            .findRecentlyUpdatedRestaurantIds(
                startTime, endTime, processedCount, batchSize);

        if (batch.isEmpty()) {
          break;
        }

        List<String> uniqueIds = batch.stream()
            .filter(id -> !processedIds.contains(id))
            .peek(processedIds::add)
            .toList();

        List<RestaurantRatingResult> results =
            reviewRepository.calculateRestaurantRatings(uniqueIds);

        results.forEach(result -> {
              try {
                reviewEventPublisher.publish(
                    RestaurantRatingUpdateEvent.of(
                        RestaurantRatingUpdatePayload.from(result)));
              } catch (Exception e) {
                logPublishError(e);
              }
            }
        );

        logPublish(results);
        processedCount += uniqueIds.size();
        logProgress();
      }
      logCompletion();
    }

    private void logPublishError(Exception e) {
      log.error("레스토랑 평점 업데이트 중 오류 발생: {}", e.getMessage(), e);
    }

    private void logPublish(List<RestaurantRatingResult> results) {
      log.info("레스토랑 평점 일괄 업데이트 이벤트 발행: {}개", results.size());
    }

    private void logProgress() {
      log.info("레스토랑 평점 업데이트 진행률: {}/{} (중복 제외 실제 처리: {}개)",
          processedCount, totalCount, processedIds.size());
    }

    private void logCompletion() {
      log.info("레스토랑 평점 업데이트 작업 완료: 총 {}개 조회됨, 중복 제외 {}개 처리됨",
          processedCount, processedIds.size());
    }
  }
}