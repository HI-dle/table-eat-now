package table.eat.now.promotion.promotion.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;
import table.eat.now.promotion.promotion.infrastructure.persistence.JpaPromotionRepository;
import table.eat.now.promotion.promotion.infrastructure.redis.PromotionRedisRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Repository
@RequiredArgsConstructor
public class PromotionRepositoryImpl implements PromotionRepository {

  private final JpaPromotionRepository jpaPromotionRepository;
  private final PromotionRedisRepository promotionRedisRepository;

  @Override
  public Promotion save(Promotion promotion) {
    return jpaPromotionRepository.save(promotion);
  }

  @Override
  public Optional<Promotion> findByPromotionUuidAndDeletedByIsNull(String promotionUuid) {
    return jpaPromotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }

  @Override
  public PaginatedResult<PromotionSearchCriteriaQuery> searchPromotion(
      PromotionSearchCriteria criteria) {
    return jpaPromotionRepository.searchPromotion(criteria);
  }

  @Override
  public List<Promotion> findAllByPromotionUuidInAndDeletedByIsNull(Set<String> promotionUuids) {
    return jpaPromotionRepository.findAllByPromotionUuidInAndDeletedByIsNull(promotionUuids);
  }

  @Override
  public ParticipateResult addUserToPromotion(PromotionParticipant participant,
      int maxCount) {
    return promotionRedisRepository.addUserToPromotion(participant, maxCount);
  }

  @Override
  public List<PromotionParticipantDto> getPromotionUsers(String promotionName) {
    return promotionRedisRepository.getPromotionUsers(promotionName);
  }
}
