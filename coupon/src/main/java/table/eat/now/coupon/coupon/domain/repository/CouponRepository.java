package table.eat.now.coupon.coupon.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.coupon.coupon.domain.criteria.CouponCriteria;
import table.eat.now.coupon.coupon.domain.entity.Coupon;

public interface CouponRepository {

  Coupon save(Coupon coupon);

  Optional<Coupon> findByCouponUuidAndDeletedAtIsNullFetchJoin(String couponUuid);

  Page<Coupon> searchCouponByPageableAndCondition(Pageable pageable, CouponCriteria criteria);

  <S extends Coupon> List<S> saveAll(Iterable<S> coupons);

  List<Coupon> findByCouponUuidsInAndDeletedAtIsNullFetchJoin(Set<String> couponUuids);
}
