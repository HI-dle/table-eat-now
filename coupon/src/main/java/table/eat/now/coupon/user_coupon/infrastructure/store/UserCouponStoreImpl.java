package table.eat.now.coupon.user_coupon.infrastructure.store;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.store.UserCouponStore;
import table.eat.now.coupon.user_coupon.infrastructure.persistence.jdbc.JdbcUserCouponRepository;
import table.eat.now.coupon.user_coupon.infrastructure.persistence.jpa.JpaEmUserCouponRepository;

@RequiredArgsConstructor
@Repository
public class UserCouponStoreImpl implements UserCouponStore {

  private final JpaEmUserCouponRepository emRepository;
  private final JdbcUserCouponRepository jdbcRepository;

  @Override
  public void optimizedSaveAll(List<UserCoupon> userCoupons) {
    emRepository.saveAll(userCoupons);
  }

  @Override
  public void batchInsert(List<UserCoupon> userCoupons) {
    jdbcRepository.batchInsert(userCoupons);
  }
}
