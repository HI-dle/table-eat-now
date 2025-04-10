package table.eat.now.coupon.coupon.application.usecase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@RequiredArgsConstructor
@Service
public class PrepareCouponIssuanceUsecase {
  private final CouponRepository couponRepository;

  public void execute() {

    LocalDateTime fromAt = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS);
    LocalDateTime toAt = fromAt.plusHours(1);
    List<Coupon> coupons = couponRepository.findCouponsStartInFromTo(fromAt, toAt);

    for (Coupon coupon : coupons) {
      prepareCouponIssuance(coupon);
    }
  }

  public void prepareCouponIssuance(Coupon coupon) {
    Duration duration = Duration.between(LocalDateTime.now(), coupon.getPeriod().getEndAt())
        .plusMinutes(10);
    if (coupon.getCount() > 0) {
      couponRepository.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    }
    if (!coupon.getAllowDuplicate()) {
      couponRepository.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
    }
  }
}
