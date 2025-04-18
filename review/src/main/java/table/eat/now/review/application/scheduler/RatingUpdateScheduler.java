package table.eat.now.review.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.service.ReviewService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RatingUpdateScheduler {

  private final ReviewService reviewService;

  @Value("${review.rating.update.batch-size}")
  private int batchSize;

  @Scheduled(cron = "${review.rating.update.cron}")
  public void updateRestaurantRatings() {
    log.info("리뷰 평점 일괄 업데이트 작업 시작 (배치 크기: {})", batchSize);

    try {
      reviewService.updateRecentlyChangedRatings(batchSize);
    } catch (Exception e) {
      log.error("평점 업데이트 작업 중 오류 발생: {}", e.getMessage(), e);
    }
    log.info("리뷰 평점 일괄 업데이트 작업 종료");
  }
}