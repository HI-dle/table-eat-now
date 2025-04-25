package table.eat.now.coupon.user_coupon.infrastructure.persistence.jpa;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;

public interface JpaUserCouponRepository extends
    JpaRepository<UserCoupon, Long>, UserCouponRepository {

  @Override
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select uc from UserCoupon uc "
      + "where uc.userCouponUuid = :userCouponUuid and uc.deletedAt is null")
  Optional<UserCoupon> findByUserCouponUuidAndDeletedAtIsNullWithLock(String userCouponUuid);

  @Override
  List<UserCoupon> findByUserCouponUuidInAndDeletedAtIsNull(Set<String> userCouponUuids);

  @Override
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select uc from UserCoupon uc "
      + "where uc.userCouponUuid in :userCouponUuids and uc.deletedAt is null")
  List<UserCoupon> findByUserCouponUuidsInAndDeletedAtIsNullWithLock(Set<String> userCouponUuids);

  @Override
  @Modifying
  @Query("update UserCoupon uc set uc.status = 'ROLLBACK', uc.preemptAt = null, uc.reservationUuid = null "
      + "where uc.deletedAt is null and uc.usedAt is null and uc.preemptAt <= :tenMinutesAgo")
  void releasePreemptionAfter10m(LocalDateTime tenMinutesAgo);
}
