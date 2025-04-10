package table.eat.now.promotion.promotionRestaurant.infrastructure.persistence.impl;


import static table.eat.now.promotion.promotionRestaurant.domain.entity.QPromotionRestaurant.promotionRestaurant;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteria;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteriaQuery;
import table.eat.now.promotion.promotionRestaurant.infrastructure.persistence.JpaPromotionRestaurantRepositoryCustom;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 11.
 */
@RequiredArgsConstructor
public class JpaPromotionRestaurantRepositoryCustomImpl implements
    JpaPromotionRestaurantRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  @Override
  public PaginatedResult<PromotionRestaurantSearchCriteriaQuery> searchPromotionRestaurant(
      PromotionRestaurantSearchCriteria criteria) {
    BooleanExpression[] conditions = new BooleanExpression[]{
        equalPromotionUuid(criteria.promotionUuid()),
        equalRestaurantUuid(criteria.restaurantUuid()),
        deletedByIsNull()
    };

    List<PromotionRestaurant> fetch = queryFactory.selectFrom(promotionRestaurant)
        .where(conditions)
        .orderBy(getOrderSpecifier(criteria))
        .offset(criteria.page())
        .limit(criteria.size())
        .fetch();

    Long totalCount = queryFactory.select(promotionRestaurant.count())
        .from(promotionRestaurant)
        .where(conditions)
        .fetchOne();

    if (totalCount == null)
      totalCount = 0L;

    List<PromotionRestaurantSearchCriteriaQuery> criteriaResponses = fetch.stream()
        .map(PromotionRestaurantSearchCriteriaQuery::from)
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
        promotionRestaurant.promotionUuid.eq(promotionUuid);
  }

  private BooleanExpression equalRestaurantUuid(String restaurantUuid) {
    return restaurantUuid == null ? null :
        promotionRestaurant.restaurantUuid.eq(restaurantUuid);
  }
  private BooleanExpression deletedByIsNull() {
    return promotionRestaurant.deletedBy.isNull();
  }


  private OrderSpecifier<?> getOrderSpecifier( PromotionRestaurantSearchCriteria criteria) {
    return "updatedAt".equals(criteria.sortBy()) ? (
        criteria.isAsc() ? promotionRestaurant.updatedAt.asc() : promotionRestaurant.updatedAt.desc())
        : (criteria.isAsc() ? promotionRestaurant.createdAt.asc() : promotionRestaurant.createdAt.desc());
  }
}