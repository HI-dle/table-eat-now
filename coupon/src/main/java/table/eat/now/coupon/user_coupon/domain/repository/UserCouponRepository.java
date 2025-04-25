package table.eat.now.coupon.user_coupon.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

public interface UserCouponRepository {
  UserCoupon save(UserCoupon userCoupon);

  Optional<UserCoupon> findByUserCouponUuidAndDeletedAtIsNull(String userCouponUuid);

  Optional<UserCoupon> findByUserCouponUuidAndDeletedAtIsNullWithLock(String userCouponUuid);

  List<UserCoupon> findByUserCouponUuidInAndDeletedAtIsNull(Set<String> userCouponUuids);

  List<UserCoupon> findByUserCouponUuidsInAndDeletedAtIsNullWithLock(Set<String> userCouponUuids);

  void releasePreemptionAfter10m(LocalDateTime tenMinutesAgo);

  Page<UserCoupon> findByUserIdAndExpiresAtAfterAndDeletedAtIsNull(
      Long userId, LocalDateTime now, Pageable pageable);

  <S extends UserCoupon> List<S> saveAll(Iterable<S> userCoupons);

  List<UserCoupon> findByReservationUuid(String reservationUuid);
}
