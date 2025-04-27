package table.eat.now.promotion.promotionuser.infrastructure.persistence.impl;


import static table.eat.now.promotion.promotionuser.domain.entity.QPromotionUser.promotionUser;
import static table.eat.now.promotion.promotionuser.infrastructure.metric.PromotionUserMetricName.PROMOTION_USER_BATCH_SAVE_LATENCY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionuser.domain.outbox.entity.PromotionUserOutbox;
import table.eat.now.promotion.promotionuser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionuser.domain.repository.search.PromotionUserSearchCriteria;
import table.eat.now.promotion.promotionuser.domain.repository.search.PromotionUserSearchCriteriaQuery;
import table.eat.now.promotion.promotionuser.infrastructure.persistence.JpaPromotionUserRepositoryCustom;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 11.
 */
@RequiredArgsConstructor
public class JpaPromotionUserRepositoryCustomImpl implements JpaPromotionUserRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final EntityManager entityManager;
  private final MeterRegistry meterRegistry;


  @Override
  public void saveAllInBatch(List<PromotionUser> promotionUsers) {
    meterRegistry.timer(PROMOTION_USER_BATCH_SAVE_LATENCY).record(() -> {
      int batchSize = 1000;
      List<PromotionUserOutbox> outboxes = new ArrayList<>();


      for (int i = 0; i < promotionUsers.size(); i++) {
        PromotionUser user = promotionUsers.get(i);
        entityManager.persist(user);

        // Outbox도 같이 생성
        String payload = createNotificationPayload(user);
        PromotionUserOutbox outbox = PromotionUserOutbox.create(user.getPromotionUserUuid(), payload);
        outboxes.add(outbox);

        if (i % batchSize == 0 && i > 0) {
          entityManager.flush();
          entityManager.clear();
          saveOutboxes(outboxes);
          outboxes.clear();
        }
      }
      entityManager.flush();
      entityManager.clear();
      saveOutboxes(outboxes);
    });
  }

  @Override
  public PaginatedResult<PromotionUserSearchCriteriaQuery> searchPromotionUser(
      PromotionUserSearchCriteria criteria) {
    BooleanExpression[] conditions = new BooleanExpression[]{
        equalPromotionUuid(criteria.promotionUuid()),
        equalUserId(criteria.userId()),
        deletedByIsNull()
    };

    List<PromotionUser> fetch = queryFactory.selectFrom(promotionUser)
        .where(conditions)
        .orderBy(getOrderSpecifier(criteria))
        .offset(criteria.page())
        .limit(criteria.size())
        .fetch();

    Long totalCount = queryFactory.select(promotionUser.count())
        .from(promotionUser)
        .where(conditions)
        .fetchOne();

    if (totalCount == null)
      totalCount = 0L;

    List<PromotionUserSearchCriteriaQuery> criteriaResponses = fetch.stream()
        .map(PromotionUserSearchCriteriaQuery::from)
        .toList();

    int totalPages = (int) Math.ceil((double) totalCount / criteriaResponses.size());

    return new PaginatedResult<>(
        criteriaResponses,
        criteria.page(),
        criteria.size(),
        totalCount,
        totalPages);
  }

  private BooleanExpression equalPromotionUuid(String promotionUuid) {
    return promotionUuid == null ? null :
        promotionUser.promotionUuid.eq(promotionUuid);
  }

  private BooleanExpression equalUserId(Long userId) {
    return userId == null ? null :
        promotionUser.userId.eq(userId);
  }
  private BooleanExpression deletedByIsNull() {
    return promotionUser.deletedBy.isNull();
  }


  private OrderSpecifier<?> getOrderSpecifier(PromotionUserSearchCriteria criteria) {
    return "updatedAt".equals(criteria.sortBy()) ? (
        criteria.isAsc() ? promotionUser.updatedAt.asc() : promotionUser.updatedAt.desc())
        : (criteria.isAsc() ? promotionUser.createdAt.asc() : promotionUser.createdAt.desc());
  }

  private void saveOutboxes(List<PromotionUserOutbox> outboxes) {
    for (PromotionUserOutbox outbox : outboxes) {
      entityManager.persist(outbox);
    }
  }
  private String createNotificationPayload(PromotionUser user) {
    Map<String, Object> payloadMap = Map.of(
        "promotionUserUuid", user.getPromotionUserUuid(),
        "promotionUuid", user.getPromotionUuid(),
        "userId", user.getUserId()
    );
    try {
      return new ObjectMapper().writeValueAsString(payloadMap);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("PromotionUser 알림 Payload 직렬화 실패", e);
    }
  }

}
