package table.eat.now.payment.payment.infrastructure.persistence;

import static table.eat.now.payment.payment.domain.entity.QPayment.payment;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import table.eat.now.payment.payment.domain.entity.PaymentStatus;
import table.eat.now.payment.payment.domain.repository.search.PaginatedResult;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsCriteria;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsResult;

@RequiredArgsConstructor
public class JpaPaymentRepositoryCustomImpl implements JpaPaymentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public PaginatedResult<SearchPaymentsResult> searchPayments(SearchPaymentsCriteria criteria) {
    BooleanExpression whereCondition = buildWhereCondition(criteria);

    List<SearchPaymentsResult> content = queryFactory
        .select(Projections.constructor(SearchPaymentsResult.class,
            payment.identifier.paymentUuid,
            payment.reference.customerId,
            payment.paymentKey,
            payment.reference.reservationId,
            payment.reference.restaurantId,
            payment.reference.reservationName,
            payment.paymentStatus.stringValue(),
            payment.amount.originalAmount,
            payment.amount.discountAmount,
            payment.amount.totalAmount,
            payment.createdAt,
            payment.approvedAt,
            payment.cancelledAt
        ))
        .from(payment)
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
        .select(payment.count())
        .from(payment)
        .where(whereCondition)
        .fetchOne();

    return count != null ? count : 0L;
  }

  private BooleanExpression buildWhereCondition(SearchPaymentsCriteria criteria) {
    return payment.deletedAt.isNull()
        .and(restaurantIdEquals(criteria.restaurantUuid()))
        .and(paymentStatusEquals((criteria.paymentStatus())))
        .and(customerIdEquals(criteria.userId()))
        .and(createdAtBetween(criteria.startDate(), criteria.endDate()));
  }

  private BooleanExpression restaurantIdEquals(String restaurantId) {
    return (restaurantId != null && !restaurantId.isEmpty()) ?
        payment.reference.restaurantId.eq(restaurantId) : null;
  }

  private BooleanExpression paymentStatusEquals(PaymentStatus paymentStatus) {
    return paymentStatus != null ? payment.paymentStatus.eq(paymentStatus) : null;
  }

  private BooleanExpression customerIdEquals(Long customerId) {
    return customerId != null ? payment.reference.customerId.eq(customerId) : null;
  }

  private BooleanExpression createdAtBetween(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
    LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

    if (start == null && end == null) {
      return null;
    }
    if (start == null) {
      return payment.createdAt.loe(end);
    }
    if (end == null) {
      return payment.createdAt.goe(start);
    }
    return payment.createdAt.between(start, end);
  }

  private OrderSpecifier<?> getOrderSpecifier(String sort, String orderBy) {
    boolean isAsc = "asc".equalsIgnoreCase(sort);

    if ("updatedAt".equals(orderBy)) {
      return isAsc ? payment.updatedAt.asc() : payment.updatedAt.desc();
    }

    return isAsc ? payment.createdAt.asc() : payment.createdAt.desc();
  }
}