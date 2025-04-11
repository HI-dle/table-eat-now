package table.eat.now.coupon.coupon.domain.repository;

import java.time.Duration;
import java.time.LocalDateTime;
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

  List<Coupon> findCouponsStartInFromTo(LocalDateTime from, LocalDateTime to);

  void setCouponCountWithTtl(String couponUuid, Integer value, Duration ttl);

  void setCouponSetWithTtl(String couponUuid, Duration ttl);

  Long decreaseCouponCount(String couponUuid);

  boolean isAlreadyIssued(String couponUuid, Long userId);

  boolean markAsIssued(String couponUuid, Long userId);

  Long increaseCouponCount(String couponUuid);

  Integer getCouponCount(String couponUuid);

  Page<Coupon> getAvailableCoupons(Pageable pageable, LocalDateTime time);
}
