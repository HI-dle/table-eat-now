package table.eat.now.coupon.coupon.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final CouponRepository couponRepository;

  @Override
  public UUID createCoupon(CreateCouponCommand command) {

    Coupon coupon = command.toEntity();
    couponRepository.save(coupon);
    return coupon.getCouponUuid();
  }
}
