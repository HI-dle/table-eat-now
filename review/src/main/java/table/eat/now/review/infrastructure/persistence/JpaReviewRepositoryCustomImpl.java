package table.eat.now.review.infrastructure.persistence;

import static table.eat.now.review.domain.entity.QReview.review;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.search.PaginatedResult;
import table.eat.now.review.domain.repository.search.SearchAdminReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchAdminReviewResult;
import table.eat.now.review.domain.repository.search.SearchReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchReviewResult;

@RequiredArgsConstructor
public class JpaReviewRepositoryCustomImpl implements JpaReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public PaginatedResult<SearchReviewResult> searchReviews(SearchReviewCriteria criteria) {

    BooleanExpression whereCondition = buildWhereCondition(criteria);

    List<SearchReviewResult> content = queryFactory
        .select(Projections.constructor(SearchReviewResult.class,
            review.reviewId,
            review.reference.customerId,
            review.reference.restaurantId,
            review.reference.serviceId,
            review.reference.serviceType.stringValue(),
            review.content.rating,
            review.content.content,
            review.visibility.isVisible,
            review.visibility.hiddenBy,
            review.visibility.hiddenByRole.stringValue(),
            review.createdAt,
            review.updatedAt))
        .from(review)
        .where(whereCondition)
        .orderBy(getOrderSpecifier(criteria.sort(), criteria.orderBy()))
        .offset(criteria.page())
        .limit(criteria.size())
        .fetch();

    Long totalElements = queryFactory
        .select(review.count())
        .from(review)
        .where(whereCondition)
        .fetchOne();

    totalElements = totalElements == null ? 0 : totalElements;
    int totalPages = (int) Math.ceil((double) totalElements / criteria.size());

    return new PaginatedResult<>(
        content,
        criteria.page(),
        criteria.size(),
        totalElements,
        totalPages
    );
  }

  @Override
  public PaginatedResult<SearchAdminReviewResult> searchAdminReviews(
      SearchAdminReviewCriteria criteria) {

    BooleanExpression whereCondition = buildWhereAdminCondition(criteria);

    List<SearchAdminReviewResult> content = queryFactory
        .select(Projections.constructor(SearchAdminReviewResult.class,
            review.reviewId,
            review.reference.customerId,
            review.reference.restaurantId,
            review.reference.serviceId,
            review.reference.serviceType.stringValue(),
            review.content.rating,
            review.content.content,
            review.visibility.isVisible,
            review.visibility.hiddenBy,
            review.visibility.hiddenByRole.stringValue(),
            review.createdAt,
            review.updatedAt))
        .from(review)
        .where(whereCondition)
        .orderBy(getOrderSpecifier(criteria.sort(), criteria.orderBy()))
        .offset(criteria.page())
        .limit(criteria.size())
        .fetch();

    Long totalElements = queryFactory
        .select(review.count())
        .from(review)
        .where(whereCondition)
        .fetchOne();

    totalElements = totalElements == null ? 0 : totalElements;
    int totalPages = (int) Math.ceil((double) totalElements / criteria.size());

    return new PaginatedResult<>(
        content,
        criteria.page(),
        criteria.size(),
        totalElements,
        totalPages
    );
  }

  private BooleanExpression buildWhereCondition(SearchReviewCriteria criteria) {
    return review.deletedAt.isNull()
        .and(customerIdEquals(criteria.userId()))
        .and(restaurantIdEquals(criteria.restaurantId()))
        .and(serviceTypeEquals(criteria.serviceType()))
        .and(ratingBetween(criteria.minRating(), criteria.maxRating()))
        .and(createdAtBetween(criteria.startDate(), criteria.endDate()))
        .and(getVisibilityUserCondition(criteria));
  }

  private BooleanExpression buildWhereAdminCondition(SearchAdminReviewCriteria criteria) {
    return review.deletedAt.isNull()
        .and(customerIdEquals(criteria.userId()))
        .and(restaurantIdEquals(criteria.restaurantId()))
        .and(serviceTypeEquals(criteria.serviceType()))
        .and(ratingBetween(criteria.minRating(), criteria.maxRating()))
        .and(createdAtBetween(criteria.startDate(), criteria.endDate()))
        .and(getVisibilityAdminCondition(criteria));
  }

  private BooleanExpression getVisibilityAdminCondition(SearchAdminReviewCriteria criteria) {
    BooleanExpression isVisible = review.visibility.isVisible.isTrue();
    String accessibleRestaurantId = criteria.accessibleRestaurantId();

    if (accessibleRestaurantId == null) {
      Boolean visibilityFilter = criteria.isVisible();
      return visibilityFilter == null ? null :
          visibilityFilter ? isVisible
              : review.visibility.isVisible.isFalse();
    }

    return isVisible.or(review.reference.restaurantId.eq(accessibleRestaurantId));
  }

  private BooleanExpression getVisibilityUserCondition(SearchReviewCriteria criteria) {
    BooleanExpression isVisible = review.visibility.isVisible.isTrue();
    Long currentUserId = criteria.currentUserId();
    Long targetUserId = criteria.userId();

    if (currentUserId == null) {
      return isVisible;
    }

    if (targetUserId != null) {
      if (currentUserId.equals(targetUserId)) {
        Boolean visibilityFilter = criteria.isVisible();
        return visibilityFilter == null
            ? null : (visibilityFilter ? isVisible : review.visibility.isVisible.isFalse());
      }
      return isVisible;
    }

    return isVisible.or(review.reference.customerId.eq(currentUserId));
  }

  private BooleanExpression customerIdEquals(Long customerId) {
    return customerId != null ? review.reference.customerId.eq(customerId) : null;
  }

  private BooleanExpression restaurantIdEquals(String restaurantId) {
    return (restaurantId != null && !restaurantId.isEmpty()) ?
        review.reference.restaurantId.eq(restaurantId) : null;
  }

  private BooleanExpression serviceTypeEquals(ServiceType serviceType) {
    return serviceType != null ? review.reference.serviceType.eq(serviceType) : null;
  }

  private BooleanExpression ratingBetween(Integer minRating, Integer maxRating) {
    if (minRating == null && maxRating == null) {
      return null;
    }
    if (minRating == null) {
      return review.content.rating.loe(maxRating);
    }
    if (maxRating == null) {
      return review.content.rating.goe(minRating);
    }
    return review.content.rating.between(minRating, maxRating);
  }

  private BooleanExpression createdAtBetween(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = getStartDatetime(startDate);
    LocalDateTime end = getEndDatetime(endDate);

    if (start == null && end == null) {
      return null;
    }
    if (start == null) {
      return review.createdAt.loe(end);
    }
    if (end == null) {
      return review.createdAt.goe(start);
    }
    return review.createdAt.between(start, end);
  }

  private static LocalDateTime getEndDatetime(LocalDate endDate) {
    return endDate != null ? endDate.atTime(LocalTime.MAX) : null;
  }

  private static LocalDateTime getStartDatetime(LocalDate startDate) {
    return startDate != null ? startDate.atStartOfDay() : null;
  }

  private OrderSpecifier<?> getOrderSpecifier(String sort, String orderBy) {
    boolean isAsc = "asc".equalsIgnoreCase(sort);
    if ("rating".equals(orderBy)) {
      return isAsc ? review.content.rating.asc() : review.content.rating.desc();
    }
    return isAsc ? review.createdAt.asc() : review.createdAt.desc();
  }
}

