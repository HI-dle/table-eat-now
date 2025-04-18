package table.eat.now.coupon.user_coupon.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

public interface UserCouponRepository {
  UserCoupon save(UserCoupon userCoupon);

  Optional<UserCoupon> findByUserCouponUuidAndDeletedAtIsNull(String userCouponUuid);

  Optional<UserCoupon> findByUserCouponUuidAndDeletedAtIsNullWithLock(String userCouponUuid);

  void releasePreemptionsAfter10m(LocalDateTime tenMinutesAgo);

  Page<UserCoupon> findByUserIdAndExpiresAtAfterAndDeletedAtIsNull(
      Long userId, LocalDateTime now, Pageable pageable);

  <S extends UserCoupon> List<S> saveAll(Iterable<S> userCoupons);
}
