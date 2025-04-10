package table.eat.now.coupon.coupon.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import table.eat.now.coupon.coupon.application.usecase.PrepareCouponIssuanceUsecase;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {
  private final PrepareCouponIssuanceUsecase prepareCouponIssuanceUsecase;

  @Scheduled(cron="0 0 * * * *")
  public void setCouponIssuanceInfo() {
    log.info("쿠폰 발급 정보::레디스에 로드 시작");
    prepareCouponIssuanceUsecase.execute();
    log.info("쿠폰 발급 정보::레디스에 로드 완료");
  }
}
