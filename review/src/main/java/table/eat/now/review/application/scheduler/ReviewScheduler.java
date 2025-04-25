package table.eat.now.review.application.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

  @Value("${review.rating.update.recent.interval}")
  private int recentMinutes;

  @Scheduled(cron = "${review.rating.update.recent.cron}")
  public void updateRestaurantRecentRatings() {
    TaskExecutor executor = executorFactory.createSchedulerExecutor(
        MetricName.RATING_UPDATE_RECENT,
        LockKey.RATING_UPDATE_RECENT
    );

    executor.execute(() -> {
          try {
            logStartBatch();
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusMinutes(recentMinutes);
            updateRestaurantRatingUseCase.execute(start, end);
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
        LocalDate today = LocalDate.now();
        LocalDateTime end = LocalDate.now().atStartOfDay();
        LocalDateTime start = today.minusDays(1).atStartOfDay();
        updateRestaurantRatingUseCase.execute(start, end);
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