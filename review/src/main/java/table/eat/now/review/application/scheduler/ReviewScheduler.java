package table.eat.now.review.application.scheduler;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.batch.CursorKey;
import table.eat.now.review.application.executor.TaskExecutorFactory;
import table.eat.now.review.application.executor.lock.LockKey;
import table.eat.now.review.application.executor.metric.MetricName;
import table.eat.now.review.application.executor.task.TaskExecutor;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScheduler {

  private final UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;
  private final TaskExecutorFactory executorFactory;

  @Scheduled(fixedDelayString = "${review.rating.update.recent.delay-ms}")
  public void updateRestaurantRecentRatings() {
    TaskExecutor executor = executorFactory.createSchedulerExecutor(
        MetricName.RATING_UPDATE_RECENT,
        LockKey.RATING_UPDATE_RECENT
    );

    executor.execute(() -> {
          try {
            logStartBatch();
            updateRestaurantRatingUseCase.execute(
                CursorKey.RATING_UPDATE_RECENT_CURSOR,
                Duration.ofMinutes(5));
          } catch (Exception e) {
            logError(e);
          }
        }
    );
  }

  @Scheduled(cron = "${review.rating.update.daily.cron}")
  public void updateRestaurantDailyRatings() {
    TaskExecutor executor = executorFactory.createSchedulerExecutor(
        MetricName.RATING_UPDATE_DAILY,
        LockKey.RATING_UPDATE_DAILY
    );

    executor.execute(() -> {
      try {
        logStartBatch();
        updateRestaurantRatingUseCase.execute(
            CursorKey.RATING_UPDATE_DAILY_CURSOR,
            Duration.ofDays(1)
        );
      } catch (Exception e) {
        logError(e);
      }
    });
  }

  private static void logStartBatch() {
    log.info("리뷰 평점 일괄 업데이트 작업 시작");
  }

  private static void logError(Exception e) {
    log.error("평점 업데이트 작업 중 오류 발생: {}", e.getMessage(), e);
  }
}