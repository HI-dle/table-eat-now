package table.eat.now.coupon.coupon.application.usecase;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.application.messaging.EventPublisher;
import table.eat.now.coupon.coupon.application.messaging.event.CouponRequestedIssueEvent;
import table.eat.now.coupon.coupon.application.usecase.dto.request.IssuePromotionCouponCommand;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.info.CouponProfile;
import table.eat.now.coupon.coupon.domain.reader.CouponReader;
import table.eat.now.coupon.coupon.domain.store.CouponStore;

@RequiredArgsConstructor
@Service
public class IssuePromotionCouponUsecase {
  private final EventPublisher<CouponRequestedIssueEvent> eventPublisher;
  private final CouponReader couponReader;
  private final CouponStore couponStore;

  public void execute(IssuePromotionCouponCommand command) {

    Coupon coupon = couponReader.findValidCouponByUuid(
        command.couponUuid())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    // 쿠폰 발급 가능일자 검증 추가하면 좋을 듯
    CouponProfile couponProfile = CouponProfile.parse(coupon);

    couponStore.requestIssue(command.toDomain(couponProfile));
    String userCouponUuid = UUID.randomUUID().toString();
    eventPublisher.publish(CouponRequestedIssueEvent.of(userCouponUuid, command.userId(), coupon));
  }
}
