package table.eat.now.promotion.promotionuser.infrastructure.persistence.outbox;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.OutboxStatus;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.PromotionUserOutbox;
import table.eat.now.promotion.promotionuser.domain.outbox.repository.PromotionUserOutboxRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
public interface JpaPromotionUserOutboxRepository extends PromotionUserOutboxRepository,
    JpaRepository<PromotionUserOutbox, Long> {

  List<PromotionUserOutbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

}
