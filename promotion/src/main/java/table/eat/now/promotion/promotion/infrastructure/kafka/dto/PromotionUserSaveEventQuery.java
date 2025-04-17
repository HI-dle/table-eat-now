package table.eat.now.promotion.promotion.infrastructure.kafka.dto;



import java.util.List;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;

public record PromotionUserSaveEventQuery(
    EventType eventType,
    List<PromotionUserSavePayloadQuery> payloads,
    CurrentUserInfoDto userInfo
) implements PromotionEvent {

  public static PromotionUserSaveEventQuery of(
      List<PromotionUserSavePayloadQuery> payloads, CurrentUserInfoDto userInfo) {
    return new PromotionUserSaveEventQuery(
        EventType.SUCCEED, payloads, userInfo);
  }

}
