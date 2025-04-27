package table.eat.now.promotion.promotionuser.infrastructure.kafka;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.OutboxStatus;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.PromotionUserOutbox;
import table.eat.now.promotion.promotionuser.domain.outbox.repository.PromotionUserOutboxRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionUserKafkaOutboxProducer {

  private static final String TOPIC = "notification-event";
  private static final String DLT_SUFFIX = "-dlt";

  private final PromotionUserOutboxRepository outboxRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Scheduled(fixedDelay = 7000)
  @Transactional
  public void publishPendingPromotionUserEvents() {
    List<PromotionUserOutbox> pendingOutboxes = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(
        OutboxStatus.PENDING);

    List<CompletableFuture<PromotionUserOutbox>> futures = pendingOutboxes.stream()
        .map(outbox -> kafkaTemplate.send(TOPIC, outbox.getAggregateId(), outbox.getPayload())
            .handle((res, ex) -> {
              if (ex != null) {
                log.error("PromotionUser 발행 실패 id = {}, retryCount = {}",
                    outbox.getId(), outbox.getRetryCount(), ex);
                outbox.incrementRetry();

                if (outbox.getRetryCount() > 3) {
                  log.warn("PromotionUser DLQ 이동 id = {}", outbox.getId());
                  kafkaTemplate.send(TOPIC + DLT_SUFFIX, outbox.getAggregateId(), outbox.getPayload());
                  outbox.modifyStatusFailed();
                }
              } else {
                outbox.modifyStatusSuccess();
              }
              return outbox;
            }))
        .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenRun(() -> {
          List<PromotionUserOutbox> updatedOutboxes = futures.stream()
              .map(CompletableFuture::join)
              .toList();
          outboxRepository.saveAll(updatedOutboxes);
        })
        .exceptionally(ex -> {
          log.error("PromotionUser Outbox 저장 중 예외 발생", ex);
          return null;
        });
  }
}

