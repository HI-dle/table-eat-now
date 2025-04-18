package table.eat.now.review.infrastructure.persistence.jpa;

import static table.eat.now.review.domain.entity.QReview.review;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.search.PaginatedResult;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;
import table.eat.now.review.domain.repository.search.SearchAdminReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchAdminReviewResult;
import table.eat.now.review.domain.repository.search.SearchReviewCriteria;
import table.eat.now.review.domain.repository.search.SearchReviewResult;

@RequiredArgsConstructor
public class JpaReviewRepositoryCustomImpl implements JpaReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<String> findRecentlyUpdatedRestaurantIds
      (LocalDateTime startTime, LocalDateTime endTime, long offset, int limit) {
    return queryFactory
        .select(review.reference.restaurantId)
        .from(review)
        .where(betweenUpdatedAt(startTime, endTime))
        .groupBy(review.reference.restaurantId)
        .orderBy(review.updatedAt.asc(), review.reference.restaurantId.asc())
        .offset(offset)
        .limit(limit)
        .fetch();
  }

  private BooleanExpression betweenUpdatedAt(
      LocalDateTime startTime, LocalDateTime endTime) {
    return review.updatedAt.goe(startTime).and(review.updatedAt.loe(endTime));
  }

  @Override
  public long countRecentlyUpdatedRestaurants(LocalDateTime startTime, LocalDateTime endTime) {
    Long count = queryFactory
        .select(review.reference.restaurantId.countDistinct())
        .from(review)
        .where(betweenUpdatedAt(startTime, endTime))
        .fetchOne();

    return count != null ? count : 0L;
  }

  @Override
  public List<RestaurantRatingResult> calculateRestaurantRatings(List<String> restaurantIds) {
    return queryFactory
        .select(Projections.constructor(RestaurantRatingResult.class,
            review.reference.restaurantId,
            Expressions.numberTemplate(BigDecimal.class, "avg({0})", review.content.rating)
        ))
        .from(review)
        .where(review.reference.restaurantId.in(restaurantIds)
            .and(review.deletedAt.isNull()))
        .groupBy(review.reference.restaurantId)
        .fetch();
  }

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
        .offset((long) criteria.page() * criteria.size())
        .limit(criteria.size())
        .fetch();

    return createPaginatedResult(content, whereCondition, criteria.page(), criteria.size());
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
        .offset((long) criteria.page() * criteria.size())
        .limit(criteria.size())
        .fetch();

    return createPaginatedResult(content, whereCondition, criteria.page(), criteria.size());
  }

  private <T> PaginatedResult<T> createPaginatedResult(
      List<T> content, BooleanExpression whereCondition, int page, int size) {

    Long totalElements = countTotalElements(whereCondition);
    int totalPages = (int) Math.ceil((double) totalElements / size);
    return new PaginatedResult<>(content, page, size, totalElements, totalPages);
  }

  private Long countTotalElements(BooleanExpression whereCondition) {
    Long count = queryFactory
        .select(review.count())
        .from(review)
        .where(whereCondition)
        .fetchOne();

    return count != null ? count : 0L;
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
    BooleanExpression isInvisible = review.visibility.isVisible.isFalse();
    Boolean optionalVisibility = criteria.isVisible();
    String accessibleRestaurantId = criteria.accessibleRestaurantId();
    boolean isMaster = criteria.isMaster();

    if (isMaster && optionalVisibility != null) {
      return optionalVisibility ?
          isVisible : isInvisible;
    }

    if (isMaster) {
      return null;
    }

    if (accessibleRestaurantId != null && optionalVisibility != null) {
      return optionalVisibility ? isVisible
          : isInvisible.and(filterByAccessibleRestaurantId(accessibleRestaurantId));
    }

    if (accessibleRestaurantId != null) {
      return isVisible.or(filterByAccessibleRestaurantId(accessibleRestaurantId));
    }

    return isVisible;
  }

  private BooleanExpression getVisibilityUserCondition(SearchReviewCriteria criteria) {
    BooleanExpression isVisible = review.visibility.isVisible.isTrue();
    BooleanExpression isInvisible = review.visibility.isVisible.isFalse();
    Long currentUserId = criteria.currentUserId();
    Long targetUserId = criteria.userId();
    Boolean optionalVisibility = criteria.isVisible();

    if (currentUserId == null) {
      return isVisible;
    }

    if (currentUserId.equals(targetUserId)) {
      return optionalVisibility == null ? null :
          optionalVisibility ? isVisible : isInvisible;
    }

    if (targetUserId != null) {
      return optionalVisibility == null ? isVisible :
          optionalVisibility ? isVisible : Expressions.FALSE;
    }

    if (optionalVisibility != null) {
      return optionalVisibility ?
          isVisible : isInvisible.and(filterByCurrentUser(currentUserId));
    }

    return isVisible.or(filterByCurrentUser(currentUserId));
  }

  private BooleanExpression filterByAccessibleRestaurantId(String accessibleRestaurantId) {
    return restaurantIdEquals(accessibleRestaurantId);
  }

  private BooleanExpression filterByCurrentUser(Long currentUserId) {
    return customerIdEquals(currentUserId);
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
    LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
    LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

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

  private OrderSpecifier<?> getOrderSpecifier(String sort, String orderBy) {
    boolean isAsc = "asc".equalsIgnoreCase(sort);
    if ("rating".equals(orderBy)) {
      return isAsc ? review.content.rating.asc() : review.content.rating.desc();
    }
    return isAsc ? review.createdAt.asc() : review.createdAt.desc();
  }
}