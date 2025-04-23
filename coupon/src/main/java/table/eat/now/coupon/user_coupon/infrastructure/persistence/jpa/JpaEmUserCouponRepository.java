package table.eat.now.coupon.user_coupon.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

@Repository
public class JpaEmUserCouponRepository {

  @PersistenceContext
  private EntityManager em;
  private static final int chunkSize = 200;

  @Transactional
  public void saveAll(List<UserCoupon> userCoupons) {

    for (int i = 0; i < userCoupons.size(); i++) {
      em.persist(userCoupons.get(i));
      if (i % chunkSize == 0) {
        em.flush();
        em.clear();
      }
    }
    em.flush();
    em.clear();
  }
}
