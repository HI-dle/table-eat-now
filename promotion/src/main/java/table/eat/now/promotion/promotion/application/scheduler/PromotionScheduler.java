package table.eat.now.promotion.promotion.application.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.event.PromotionEventPublisher;
import table.eat.now.promotion.promotion.application.event.produce.PromotionScheduleEvent;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 25.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PromotionScheduler {

  private final PromotionRepository promotionRepository;
  private final PromotionEventPublisher publisher;

  @Scheduled(fixedDelay = 60000) // 1분마다 확인
  public void sendKafkaPromotionChangeStatus() {
    List<String> promotionUuidList = promotionRepository.pollScheduleQueue();

    log.info("실행 한다 {}", promotionUuidList.size());
    for (String promotionUuid : promotionUuidList) {
      Promotion promotion = promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid)
          .orElseThrow(() ->
              CustomException.from(PromotionErrorCode.INVALID_PROMOTION_UUID));

      publisher.publish(PromotionScheduleEvent.from(promotion));
    }
  }
}
