package table.eat.now.coupon.coupon.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.coupon.coupon.domain.criteria.CouponCriteria;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface CouponRepositoryCustom {

  Page<Coupon> searchCouponByPageableAndCondition(Pageable pageable, CouponCriteria criteria);
}
