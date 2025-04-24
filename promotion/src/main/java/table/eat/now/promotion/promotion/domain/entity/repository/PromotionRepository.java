package table.eat.now.promotion.promotion.domain.entity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionRepository {

  Promotion save(Promotion promotion);
  Optional<Promotion> findByPromotionUuidAndDeletedByIsNull(String promotionUuid);

  PaginatedResult<PromotionSearchCriteriaQuery> searchPromotion(PromotionSearchCriteria criteria);

  List<Promotion> findAllByPromotionUuidInAndDeletedByIsNull(Set<String> promotionUuids);

  ParticipateResult addUserToPromotion(PromotionParticipant participant, int maxCount);

  List<PromotionParticipantDto> getPromotionUsers(String promotionName);

  List<String> pollScheduleQueue();
  void addScheduleQueue(String promotionUuid, LocalDateTime triggerTime);

}
