package table.eat.now.review.application.usecase;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  @Value("${review.rating.update.batch-size}")
  private int batchSize;
  private static final int RECENT_MINUTES = 5;

  private final ReviewRepository reviewRepository;
  private final ReviewEventPublisher reviewEventPublisher;

  @Transactional(readOnly = true)
  public void execute() {
    LocalDateTime startTime = LocalDateTime.now().minusMinutes(RECENT_MINUTES);
    LocalDateTime endTime = LocalDateTime.now();

    long totalCount = getRecentlyUpdatedRestaurantCounts(startTime, endTime);

    if (totalCount == 0) {
      logNoRestaurantsToUpdate();
      return;
    }

    logStartBatchUpdate(totalCount);
    BatchProcessor processor =
        new BatchProcessor(totalCount, batchSize, startTime, endTime);
    processor.process();
  }

  private long getRecentlyUpdatedRestaurantCounts(
      LocalDateTime startTime, LocalDateTime endTime) {
    return reviewRepository.countRecentlyUpdatedRestaurants(startTime, endTime);
  }

  private void logNoRestaurantsToUpdate() {
    log.info("업데이트할 레스토랑 평점이 없습니다.");
  }

  private void logStartBatchUpdate(long totalCount) {
    log.info("총 {}개 레스토랑의 평점을 배치 단위로 업데이트합니다.", totalCount);
  }

  private class BatchProcessor {

    private final long totalCount;
    private final int batchSize;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Set<String> processedIds;
    private long processedCount;

    BatchProcessor(
        long totalCount, int batchSize, LocalDateTime startTime, LocalDateTime endTime) {
      this.totalCount = totalCount;
      this.batchSize = batchSize;
      this.startTime = startTime;
      this.endTime = endTime;
      this.processedIds = new HashSet<>();
      this.processedCount = 0;
    }

    void process() {
      while (processedCount < totalCount) {
        if (processBatch()) {
          logProgress();
        }
      }
      logCompletion();
    }

    private boolean processBatch() {
      List<String> batch = fetchBatch();
      if (batch.isEmpty()) {
        return false;
      }
      processRestaurantBatch(batch);
      processedCount += batch.size();
      return true;
    }

    private List<String> fetchBatch() {
      return reviewRepository
          .findRecentlyUpdatedRestaurantIds(startTime, endTime, processedCount, batchSize);
    }

    private void processRestaurantBatch(List<String> batch) {
      List<String> uniqueIds = extractUniqueRestaurantIds(batch);
      if (!uniqueIds.isEmpty()) {
        updateAndPublishRatings(uniqueIds);
      }
    }

    private List<String> extractUniqueRestaurantIds(List<String> restaurantIds) {
      return restaurantIds.stream()
          .filter(id -> !processedIds.contains(id))
          .map(id -> {
            processedIds.add(id);
            return id;
          })
          .toList();
    }

    private void updateAndPublishRatings(List<String> uniqueRestaurantIds) {
      try {
        calculateAndPublishRatings(uniqueRestaurantIds);
      } catch (Exception e) {
        logPublishError(e);
      }
    }

    private void calculateAndPublishRatings(List<String> restaurantIds) {
      List<RestaurantRatingResult> results =
          reviewRepository.calculateRestaurantRatings(restaurantIds);
      if (!results.isEmpty()) {
        reviewEventPublisher.publish(
            RestaurantRatingUpdateEvent.of(RestaurantRatingUpdatePayload.from(results)));
        logPublish(results);
      }
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