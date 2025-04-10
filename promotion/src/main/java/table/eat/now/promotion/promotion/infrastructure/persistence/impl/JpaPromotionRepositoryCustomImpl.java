package table.eat.now.promotion.promotion.infrastructure.persistence.impl;

import static table.eat.now.promotion.promotion.domain.entity.QPromotion.*;
import static table.eat.now.promotion.promotionRestaurant.domain.entity.QPromotionRestaurant.promotionRestaurant;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;
import table.eat.now.promotion.promotion.infrastructure.persistence.JpaPromotionRepositoryCustom;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
@RequiredArgsConstructor
public class JpaPromotionRepositoryCustomImpl implements JpaPromotionRepositoryCustom {

  private final JPAQueryFactory queryFactory;


  @Override
  public PaginatedResult<PromotionSearchCriteriaQuery> searchPromotion(PromotionSearchCriteria criteria) {
    BooleanExpression[] conditions = new BooleanExpression[] {
        equalPromotionName(criteria.promotionName()),
        containsDescription(criteria.description()),
        goeStartTime(criteria.startTime()),
        loeEndTime(criteria.endTime()),
        equalPromotionStatus(criteria.promotionStatus()),
        equalPromotionType(criteria.promotionType()),
        deletedByIsNull()
    };

    List<Promotion> fetch = queryFactory.selectFrom(promotion)
        .where(conditions)
        .orderBy(getOrderSpecifier(criteria))
        .offset(criteria.page())
        .limit(criteria.size())
        .fetch();

    Long totalCount = queryFactory.select(promotion.count())
        .from(promotion)
        .where(conditions)
        .fetchOne();

    if (totalCount == null) totalCount = 0L;

    List<PromotionSearchCriteriaQuery> criteriaResponses = fetch.stream()
        .map(PromotionSearchCriteriaQuery::from)
        .toList();

    int totalPages = (int) Math.ceil((double) totalCount / criteriaResponses.size());

    return new PaginatedResult<>(
        criteriaResponses,
        criteria.page(),
        criteria.size(),
        totalCount,
        totalPages);
  }

  private BooleanExpression equalPromotionName(String promotionName) {
    return promotionName == null ? null : promotion.details.promotionName.eq(promotionName);
  }
  private BooleanExpression containsDescription(String description) {
    return description == null ? null :
        promotion.details.description.containsIgnoreCase(description);
  }
  private BooleanExpression goeStartTime(LocalDateTime startTime) {
    return startTime == null ? null : promotion.period.startTime.goe(startTime);
  }
  private BooleanExpression loeEndTime(LocalDateTime endTime) {
    return endTime == null ? null : promotion.period.endTime.loe(endTime);
  }
  private BooleanExpression equalPromotionStatus(String promotionStatus) {
    return promotionStatus == null ? null :
        promotion.promotionStatus.stringValue().containsIgnoreCase(promotionStatus);
  }
  private BooleanExpression equalPromotionType(String promotionType) {
    return promotionType == null ? null :
        promotion.promotionType.stringValue().containsIgnoreCase(promotionType);
  }
  private BooleanExpression deletedByIsNull() {
    return promotionRestaurant.deletedBy.isNull();
  }

  private OrderSpecifier<?> getOrderSpecifier(PromotionSearchCriteria criteria) {
    return "updatedAt".equals(criteria.sortBy()) ? (
        criteria.isAsc() ? promotion.updatedAt.asc() : promotion.updatedAt.desc())
        : (criteria.isAsc() ? promotion.createdAt.asc() : promotion.createdAt.desc());
  }
}
