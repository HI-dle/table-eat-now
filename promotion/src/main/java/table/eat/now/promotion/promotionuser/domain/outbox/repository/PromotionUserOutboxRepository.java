package table.eat.now.promotion.promotionuser.domain.outbox.repository;

import java.util.List;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.OutboxStatus;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.PromotionUserOutbox;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
public interface PromotionUserOutboxRepository {

  List<PromotionUserOutbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);
  <S extends PromotionUserOutbox> List<S> saveAll(Iterable<S> promotionUserOutboxes);

}
