package table.eat.now.review.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.helper.LockExecutor;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScheduler {

  private final UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;
  private final LockExecutor lockExecutor;
  private static final String RESTAURANT_RATING_UPDATE_LOCK_KEY =
      "review:scheduler:restaurant-rating-update";

  @Scheduled(cron = "${review.rating.update.cron}")
  public void updateRestaurantRatings() {
    lockExecutor.execute(RESTAURANT_RATING_UPDATE_LOCK_KEY, () -> {
      try {
        long startTime = System.nanoTime();
        logStartBatch();
        updateRestaurantRatingUseCase.execute();
        long endTime = System.nanoTime();
        logCompleteBatch(endTime, startTime);
      } catch (Exception e) {
        logError(e);
      }
    });
  }

  private static void logCompleteBatch(long endTime, long startTime) {
    long duration = (endTime - startTime) / 1_000_000;
    log.info("리뷰 평점 일괄 업데이트 작업 종료. 소요 시간: {}ms ({}초)", duration, duration / 1000.0);
  }

  private static void logError(Exception e) {
    log.error("평점 업데이트 작업 중 오류 발생: {}", e.getMessage(), e);
  }

  private static void logStartBatch() {
    log.info("리뷰 평점 일괄 업데이트 작업 시작");
  }
}