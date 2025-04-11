package table.eat.now.coupon.user_coupon.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;

public interface JpaUserCouponRepository extends
    JpaRepository<UserCoupon, Long>, UserCouponRepository {

}
