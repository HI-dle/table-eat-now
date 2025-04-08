package table.eat.now.coupon.coupon.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

public interface JpaCouponRepository extends JpaRepository<Coupon, Long>, CouponRepository {

}
