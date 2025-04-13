package table.eat.now.coupon.user_coupon.application.usecase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;

@RequiredArgsConstructor
@Service
public class ReleaseUserCouponUsecase {
  private final UserCouponRepository userCouponRepository;

  @Transactional
  public void execute() {
    LocalDateTime tenMinutesAgo = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(10);
    userCouponRepository.releasePreemptionsAfter10m(tenMinutesAgo);
  }
}
