package table.eat.now.coupon.coupon.application.usecase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@RequiredArgsConstructor
@Service
public class PrepareCouponIssuanceUsecase {
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  public void execute() {

    LocalDateTime fromAt = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS);
    LocalDateTime toAt = fromAt.plusHours(1);
    List<Coupon> coupons = couponReader.findCouponsByIssueStartAtBtwAndHotPromo(fromAt, toAt);

    for (Coupon coupon : coupons) {
      prepareCouponIssuance(coupon);
    }
  }

  private void prepareCouponIssuance(Coupon coupon) {

    Duration duration = TimeProvider.getDuration(coupon.getPeriod().getIssueEndAt(), 60);

    if (coupon.hasStockCount()) {
      couponStore.setCouponCountWithTtl(coupon.getCouponUuid(), coupon.getCount(), duration);
    }
    if (!coupon.getAllowDuplicate()) {
      couponStore.setCouponSetWithTtl(coupon.getCouponUuid(), duration);
    }
  }
}
