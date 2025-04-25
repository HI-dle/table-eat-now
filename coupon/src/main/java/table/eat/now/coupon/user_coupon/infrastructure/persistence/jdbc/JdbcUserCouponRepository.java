package table.eat.now.coupon.user_coupon.infrastructure.persistence.jdbc;

import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;

@RequiredArgsConstructor
@Repository
public class JdbcUserCouponRepository {

  private final JdbcTemplate jdbcTemplate;
  private static final int BATCHSIZE = 500;
  private static final String INSERT_QUERY = """
    insert into p_user_coupon 
        (coupon_uuid, user_coupon_uuid, user_id, name, status, expires_at, 
         created_at, created_by, updated_at, updated_by) 
    values 
        (?, ?, ?, ?, 'ISSUED', ?, now(), 0, now(), 0);
""";

  @Transactional
  public void batchInsert(List<UserCoupon> userCoupons) {
    jdbcTemplate.batchUpdate(
        INSERT_QUERY,
        userCoupons,
        BATCHSIZE,
        (ps, uc) -> {
          ps.setString(1, uc.getCouponUuid());
          ps.setString(2, uc.getUserCouponUuid());
          ps.setLong(3, uc.getUserId());
          ps.setString(4, uc.getName());
          ps.setTimestamp(5, Timestamp.valueOf(uc.getExpiresAt()));
        });
  }
}
