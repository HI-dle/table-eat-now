package table.eat.now.coupon.coupon.infrastructure.persistence.jpa;

import static table.eat.now.coupon.coupon.domain.entity.QCoupon.coupon;
import static table.eat.now.coupon.coupon.infrastructure.persistence.jpa.utils.QuerydslUtil.nullSafeBuilder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.criteria.CouponCriteria;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponType;

@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Coupon> searchCouponByPageableAndCondition(
      Pageable pageable, CouponCriteria criteria) {

    OrderSpecifier[] orderSpecifiers = createOrderSpecifiers(pageable.getSort());

    List<Coupon> dtoList = queryFactory
        .selectFrom(coupon)
        .join(coupon.policy).fetchJoin()
        .where(
          searchCondition(criteria)
        )
        .orderBy(orderSpecifiers)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(coupon.count())
        .from(coupon)
        .where(
            searchCondition(criteria)
        );

    return PageableExecutionUtils.getPage(dtoList, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder searchCondition(CouponCriteria criteria) {
    return betweenPeriod(criteria.fromAt(), criteria.toAt())
        .and(eqType(criteria.type()));
  }

  public BooleanBuilder betweenPeriod(LocalDateTime fromAt, LocalDateTime toAt) {
    if (fromAt == null && toAt == null) {
      return new BooleanBuilder();
    }
    if (fromAt == null) {
      return new BooleanBuilder(coupon.period.startAt.before(toAt));
    }
    if (toAt == null) {
      return new BooleanBuilder(coupon.period.endAt.after(fromAt));
    }
    BooleanExpression isWithinRange =
        coupon.period.startAt.before(toAt).and(coupon.period.endAt.after(fromAt));
    return new BooleanBuilder(isWithinRange);
  }

  public BooleanBuilder eqType(String type)  {
    return nullSafeBuilder(() -> coupon.type.eq(CouponType.valueOf(type)));
  }

  private OrderSpecifier[] createOrderSpecifiers(Sort sort) {

    return sort.stream()
        .map(order -> {
          Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
          return Arrays.stream(SortType.values())
              .filter(enumValue -> enumValue.checkIfMatched(order.getProperty()))
              .findAny()
              .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_SORT_CONDITION))
              .getOrderSpecifier(direction);
        })
        .toArray(OrderSpecifier[]::new);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  enum SortType {
    ENDAT((direction) -> new OrderSpecifier<>(direction, coupon.period.endAt)),
    STARTAT((direction) -> new OrderSpecifier<>(direction, coupon.period.startAt));

    private final Function<Order, OrderSpecifier> typedOrderSpecifier;

    private OrderSpecifier<?> getOrderSpecifier(Order direction) {
      return typedOrderSpecifier.apply(direction);
    }

    private boolean checkIfMatched(String property) {
      return this.name().equals(property
          .replaceAll("_", "")
          .toUpperCase());
    }
  }
}
