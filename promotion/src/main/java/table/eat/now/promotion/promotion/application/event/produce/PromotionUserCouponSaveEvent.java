package table.eat.now.promotion.promotion.application.event.produce;



import java.util.List;
import lombok.Builder;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;
@Builder
public record PromotionUserCouponSaveEvent(
    EventType eventType,
    List<PromotionUserSavePayload> payloads,
    CurrentUserInfoDto userInfo,
    String couponUuid
) implements PromotionEvent {

  public static PromotionUserCouponSaveEvent of(PromotionUserSaveEvent event, String couponUuid) {
    return PromotionUserCouponSaveEvent.builder()
        .eventType(event.eventType())
        .payloads(event.payloads())
        .userInfo(event.userInfo())
        .couponUuid(couponUuid)
        .build();
  }
}
