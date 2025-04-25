package table.eat.now.promotion.promotion.application.event.produce;


import lombok.Builder;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotion.application.dto.request.ParticipatePromotionUserInfo;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

@Builder
public record PromotionUserCouponSaveEvent(
    EventType eventType,
    PromotionUserSavePayload payload,
    CurrentUserInfoDto userInfo,
    String couponUuid
) implements PromotionEvent {

  public static PromotionUserCouponSaveEvent of(
      ParticipatePromotionUserInfo info, Promotion promotion, CurrentUserInfoDto userInfo) {
    return PromotionUserCouponSaveEvent.builder()
        .eventType(EventType.PROMOTION_PARTICIPATED_COUPON)
        .payload(PromotionUserSavePayload.from(info.userId(), promotion.getCouponUuid()))
        .userInfo(userInfo)
        .couponUuid(promotion.getCouponUuid())
        .build();
  }

}
