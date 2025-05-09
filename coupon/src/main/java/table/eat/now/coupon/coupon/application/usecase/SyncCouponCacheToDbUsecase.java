package table.eat.now.coupon.coupon.application.usecase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.coupon.application.utils.TimeProvider;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@RequiredArgsConstructor
@Service
public class SyncCouponCacheToDbUsecase {

  public static final Integer THRESHOLD_MINUTES = 5;
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  @Transactional
  public void execute() {

    LocalDateTime min5Ago = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(THRESHOLD_MINUTES);
    long threshold = TimeProvider.getEpochMillis(min5Ago);
    Set<String> expiredCouponCacheKeys = couponReader.getDirtyCouponKeysForSync(threshold);
    couponStore.deleteDirtyCouponKeysByScore(threshold);

    List<Coupon> couponCaches = couponReader.getValidCouponCachesBy(expiredCouponCacheKeys.stream().toList());

    for (Coupon coupon : couponCaches) {
      couponStore.updateIssuedCount(coupon.getCouponUuid(), coupon.getIssuedCount(), coupon.getVersion());
    }
  }
}
