package table.eat.now.coupon.coupon.domain.reader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.coupon.coupon.domain.criteria.CouponCriteria;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.entity.CouponLabel;

public interface CouponReader {

  Optional<Coupon> findValidCouponByUuid(String couponUuid);

  Page<Coupon> searchCouponByPageableAndCondition(Pageable pageable, CouponCriteria criteria);

  List<Coupon> getValidCouponsByUuids(Set<String> couponUuids);

  List<Coupon> findCouponsByIssueStartAtBtwAndHotPromo(LocalDateTime from, LocalDateTime to);

  Integer getCouponCount(String couponUuid);

  Page<Coupon> getAvailableGeneralCoupons(Pageable pageable, LocalDateTime time);

  List<Coupon> getIssuableCouponsCacheIn(CouponLabel label);

  boolean isAlreadyIssued(String couponUuid, Long userId);

  Set<String> getDirtyCouponKeysForSync(long threshold);

  List<Coupon> getValidCouponCachesBy(List<String> couponKeys);
}
