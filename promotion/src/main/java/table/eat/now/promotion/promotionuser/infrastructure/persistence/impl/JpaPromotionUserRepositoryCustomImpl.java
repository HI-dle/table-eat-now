package table.eat.now.promotion.promotionuser.infrastructure.persistence.impl;


import static table.eat.now.promotion.promotionuser.domain.entity.QPromotionUser.promotionUser;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;
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


  @Override
  public void saveAllInBatch(List<PromotionUser> promotionUsers) {
    int batchSize = 1000;
    //batchSize만큼 persist 후 flush+clear 반복
    for (int i = 0; i < promotionUsers.size(); i++) {
      entityManager.persist(promotionUsers.get(i));
      if (i % batchSize == 0 && i > 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }
    entityManager.flush();
    entityManager.clear();
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

}
