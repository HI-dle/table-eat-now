package table.eat.now.promotion.promotionUser.infrastructure.persistence.impl;


import static table.eat.now.promotion.promotionUser.domain.entity.QPromotionUser.promotionUser;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteria;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteriaQuery;
import table.eat.now.promotion.promotionUser.infrastructure.persistence.JpaPromotionUserRepositoryCustom;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 11.
 */
@RequiredArgsConstructor
public class JpaPromotionUserRepositoryCustomImpl implements JpaPromotionUserRepositoryCustom {

  private final JPAQueryFactory queryFactory;

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
