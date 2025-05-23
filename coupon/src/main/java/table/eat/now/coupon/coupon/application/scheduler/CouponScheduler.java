package table.eat.now.coupon.coupon.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.aop.annotation.DistributedLock;
import table.eat.now.coupon.coupon.application.usecase.PrepareCouponIssuanceUsecase;
import table.eat.now.coupon.coupon.application.usecase.PrepareDailyCouponCacheUsecase;
import table.eat.now.coupon.coupon.application.usecase.SyncCouponCacheToDbUsecase;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {
  private final PrepareCouponIssuanceUsecase prepareCouponIssuanceUsecase;
  private final PrepareDailyCouponCacheUsecase prepareDailyCouponCacheUsecase;
  private final SyncCouponCacheToDbUsecase syncCouponCacheToDbUsecase;

  @DistributedLock(subPrefix = "schedule:hourlycaching", waitTime = 0L, leaseTime = 60)
  @Scheduled(cron="0 0 * * * *")
  public void setCouponIssuanceInfo() {
    log.info("쿠폰 발급 정보::레디스에 로드 시작");
    prepareCouponIssuanceUsecase.execute();
    log.info("쿠폰 발급 정보::레디스에 로드 완료");
  }

  @DistributedLock(subPrefix = "schedule:dailycaching", waitTime = 0L, leaseTime = 60)
  @Scheduled(cron="0 0 0/23 * * *")
  public void prepareDailyCouponCache() {
    prepareDailyCouponCacheUsecase.execute();
  }

  @DistributedLock(subPrefix = "schedule:sync:caching:db", waitTime = 0L, leaseTime = 60)
  @Scheduled(cron="0 * * * * *")
  public void syncCouponCacheToDb() {
    syncCouponCacheToDbUsecase.execute();
  }
}
