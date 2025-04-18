package table.eat.now.review.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.usecase.UpdateRestaurantRatingUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScheduler {

  private final UpdateRestaurantRatingUseCase updateRestaurantRatingUseCase;

  @Value("${review.rating.update.batch-size}")
  private int batchSize;

  @Scheduled(cron = "${review.rating.update.cron}")
  public void updateRestaurantRatings() {
    try {
      updateRestaurantRatingUseCase.execute(batchSize);
      log.info("리뷰 평점 일괄 업데이트 작업 종료");
    } catch (Exception e) {
      log.error("평점 업데이트 작업 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}