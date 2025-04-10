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
				.where(
						review.deletedAt.isNull(),
						customerIdEquals(criteria.userId()),
						restaurantIdEquals(criteria.restaurantId()),
						serviceTypeEquals(criteria.serviceType()),
						ratingBetween(criteria.minRating(), criteria.maxRating()),
						createdAtBetween(criteria.startDate(), criteria.endDate()),
						getVisibilityCondition(criteria)
				)
				.orderBy(getOrderSpecifier(criteria))
				.offset(criteria.page())
				.limit(criteria.size())
				.fetch();

		// 전체 개수 조회
		Long totalElements = queryFactory
				.select(review.count())
				.from(review)
				.where(
						review.deletedAt.isNull(),
						customerIdEquals(criteria.userId()),
						restaurantIdEquals(criteria.restaurantId()),
						serviceTypeEquals(criteria.serviceType()),
						ratingBetween(criteria.minRating(), criteria.maxRating()),
						createdAtBetween(criteria.startDate(), criteria.endDate()),
						getVisibilityCondition(criteria)
				)
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

	private BooleanExpression getVisibilityCondition(SearchReviewCriteria criteria) {
		BooleanExpression isVisible = review.visibility.isVisible.isTrue();

		if (criteria.currentUserId() == null) {
			return isVisible;
		}

		if (criteria.userId() != null) {
			if (criteria.userId().equals(criteria.currentUserId())) {
				return criteria.isVisible() == null ?
						null :
						(criteria.isVisible() ? isVisible : review.visibility.isVisible.isFalse());
			}
			return isVisible;
		}

		return review.visibility.isVisible.isTrue()
				.or(review.reference.customerId.eq(criteria.currentUserId()));
	}

	private BooleanExpression customerIdEquals(Long customerId) {
		return customerId == null ? null : review.reference.customerId.eq(customerId);
	}

	private BooleanExpression restaurantIdEquals(String restaurantId) {
		return (restaurantId == null || restaurantId.isEmpty()) ?
				null : review.reference.restaurantId.eq(restaurantId);
	}

	private BooleanExpression serviceTypeEquals(ServiceType serviceType) {
		return serviceType == null ? null : review.reference.serviceType.eq(serviceType);
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
		if (startDate == null && endDate == null) {
			return null;
		}

		if (startDate == null) {
			LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
			return review.createdAt.loe(endDateTime);
		}

		if (endDate == null) {
			LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
			return review.createdAt.goe(startDateTime);
		}

		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
		return review.createdAt.between(startDateTime, endDateTime);
	}

	private OrderSpecifier<?> getOrderSpecifier(SearchReviewCriteria criteria) {
		if ("rating".equals(criteria.orderBy())) {
			return criteria.sort() != null && criteria.sort().equalsIgnoreCase("asc") ?
					review.content.rating.asc() : review.content.rating.desc();
		}

		return criteria.sort() != null && criteria.sort().equalsIgnoreCase("asc") ?
				review.createdAt.asc() : review.createdAt.desc();
	}
}

