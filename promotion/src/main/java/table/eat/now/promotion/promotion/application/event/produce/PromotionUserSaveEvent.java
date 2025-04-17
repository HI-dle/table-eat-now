package table.eat.now.promotion.promotion.application.event.produce;



import java.util.List;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;

public record PromotionUserSaveEvent(
    EventType eventType,
    List<PromotionUserSavePayload> payloads,
    CurrentUserInfoDto userInfo
) implements PromotionEvent {

  public static PromotionUserSaveEvent of(
      List<PromotionUserSavePayload> payloads, CurrentUserInfoDto userInfo) {
    return new PromotionUserSaveEvent(
        EventType.SUCCEED, payloads, userInfo);
  }

}
