package table.eat.now.coupon.coupon.domain.criteria;

import static table.eat.now.coupon.coupon.domain.entity.QCoupon.coupon;
import static table.eat.now.coupon.coupon.infrastructure.persistence.jpa.utils.QuerydslUtil.nullSafeBuilder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.coupon.coupon.domain.entity.CouponType;

@Builder
public record CouponCriteria(
    LocalDateTime fromAt,
    LocalDateTime toAt,
    String type
) {

  public BooleanBuilder betweenPeriod() {
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

  public BooleanBuilder eqType()  {
    return nullSafeBuilder(() -> coupon.type.eq(CouponType.valueOf(type)));
  }
}
