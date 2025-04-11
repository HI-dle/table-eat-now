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
        .orderBy(getOrderSpecifier(criteria))
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
        .and(getVisibilityCondition(criteria));
  }

  private BooleanExpression getVisibilityCondition(SearchReviewCriteria criteria) {
    BooleanExpression isVisible = review.visibility.isVisible.isTrue();
    Long currentUserId = criteria.currentUserId();
    Long targetUserId = criteria.userId();

    if (currentUserId == null) {
      return isVisible;
    }

    //특정 유저의 리뷰에 접근 시
    if (targetUserId != null) {
      //현재 로그인 한 유저와 같다면 (내 리뷰에 접근한다면)
      if (currentUserId.equals(targetUserId)) {
        //필터에 적용된 공개 여부를 따릅니다. (isVisible : true - 공개 , false - 비공개)
        Boolean visibilityFilter = criteria.isVisible();

        //유저가 해당 필터를 설정하지 않았다면
        return visibilityFilter == null
            ? null // 전체를 반환하고
            // 아니면 필터값을 따릅니다.
            : (visibilityFilter ? isVisible : review.visibility.isVisible.isFalse());
      }
      //다른 유저의 리뷰에 접근한다면, 공개상태의 리뷰만 반환합니다.
      return isVisible;
    }

    //특정 유저에 대한 접근이 아닌, 전체 접근인 경우에는
    //공개된 다른 유저의 리뷰 + 내 전체 리뷰를 반환합니다.
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

  private OrderSpecifier<?> getOrderSpecifier(SearchReviewCriteria criteria) {
    boolean isAsc = "asc".equalsIgnoreCase(criteria.sort());
    if ("rating".equals(criteria.orderBy())) {
      return isAsc ? review.content.rating.asc() : review.content.rating.desc();
    }
    return isAsc ? review.createdAt.asc() : review.createdAt.desc();
  }
}

