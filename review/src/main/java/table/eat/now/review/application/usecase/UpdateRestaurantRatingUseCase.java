package table.eat.now.review.application.usecase;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final ReviewRepository reviewRepository;
  private final ReviewEventPublisher reviewEventPublisher;

  @Transactional(readOnly = true)
  public void execute(int batchSize) {
    LocalDateTime updatedAfter = LocalDateTime.now().minusMinutes(5);
    long totalCount = getTotalUpdatedRestaurantCount(updatedAfter);

    if (totalCount == 0) {
      return;
    }

    log.info("총 {}개 레스토랑의 평점을 배치 단위로 업데이트합니다.", totalCount);
    processBatchUpdates(updatedAfter, totalCount, batchSize);
  }

  private void processBatchUpdates(LocalDateTime updatedAfter, long totalCount, int batchSize) {
    Set<String> processedRestaurantIds = new HashSet<>();
    long processedCount = 0;

    while (processedCount < totalCount) {
      List<String> restaurantBatch = fetchRestaurantBatch(updatedAfter, processedCount, batchSize);
      if (restaurantBatch.isEmpty()) {
        break;
      }

      List<String> uniqueRestaurants = filterUniqueRestaurants(restaurantBatch, processedRestaurantIds);
      if (!uniqueRestaurants.isEmpty()) {
        processRatingUpdates(uniqueRestaurants);
      }

      processedCount += restaurantBatch.size();
      logProgress(processedCount, totalCount, processedRestaurantIds.size());
    }

    logCompletion(processedCount, processedRestaurantIds.size());
  }

  private List<String> fetchRestaurantBatch(LocalDateTime updatedAfter, long offset, int limit) {
    return reviewRepository.findRecentlyUpdatedRestaurantIds(updatedAfter, offset, limit);
  }

  private List<String> filterUniqueRestaurants(List<String> restaurantIds, Set<String> processedIds) {
    return restaurantIds.stream()
        .filter(id -> !processedIds.contains(id))
        .peek(processedIds::add)
        .collect(Collectors.toList());
  }

  private void processRatingUpdates(List<String> uniqueRestaurantIds) {
    try {
      List<RestaurantRatingResult> results = reviewRepository.calculateRestaurantRatings(uniqueRestaurantIds);
      if (!results.isEmpty()) {
        publishRatingUpdates(results);
      }
    } catch (Exception e) {
      log.error("레스토랑 평점 업데이트 배치 처리 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  private void publishRatingUpdates(List<RestaurantRatingResult> results) {
    reviewEventPublisher.publish(
        RestaurantRatingUpdateEvent.of(RestaurantRatingUpdatePayload.from(results)));
    log.info("레스토랑 평점 일괄 업데이트 이벤트 발행: {}개", results.size());
  }

  private void logProgress(long processed, long total, int uniqueCount) {
    log.info("레스토랑 평점 업데이트 진행률: {}/{} (중복 제외 실제 처리: {}개)",
        processed, total, uniqueCount);
  }

  private void logCompletion(long totalProcessed, int uniqueProcessed) {
    log.info("레스토랑 평점 업데이트 작업 완료: 총 {}개 조회됨, 중복 제외 {}개 처리됨",
        totalProcessed, uniqueProcessed);
  }

  private long getTotalUpdatedRestaurantCount(LocalDateTime updatedAfter) {
    return reviewRepository.countRecentlyUpdatedRestaurants(updatedAfter);
  }
}