package table.eat.now.coupon.coupon.application.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.coupon.domain.command.CouponCachingAndIndexing;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@RequiredArgsConstructor
@Service
public class PrepareDailyCouponCacheUsecase {
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  @Transactional(readOnly = true)
  public void execute() {

    LocalDate today = LocalDate.now();
    LocalDateTime fromAt = today.plusDays(1).atStartOfDay();
    LocalDateTime toAt = fromAt.plusDays(1);

    List<Coupon> coupons = couponReader.findCouponsByIssueStartAtBtwAndHotPromo(fromAt, toAt);
    List<CouponCachingAndIndexing> command = coupons.stream()
        .map(CouponCachingAndIndexing::from)
        .toList();

    couponStore.insertCouponsCacheAndSubIndex(command);
  }
}
