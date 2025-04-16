package table.eat.now.promotion.promotionuser.infrastructure.kafka.dto;



import java.util.List;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionuser.application.event.EventType;
import table.eat.now.promotion.promotionuser.application.event.PromotionEvent;
import table.eat.now.promotion.promotionuser.application.event.dto.PromotionUserSaveEventInfo;
import table.eat.now.promotion.promotionuser.application.event.dto.PromotionUserSavePayloadInfo;


public record PromotionUserSaveEvent(
    EventType eventType,
    List<PromotionUserSavePayload> payloads,
    CurrentUserInfoDto userInfo
) implements PromotionEvent {

  public static PromotionUserSaveEventInfo toApplication(PromotionUserSaveEvent promotionUserSaveEvent) {
    return PromotionUserSaveEventInfo.builder()
        .eventType(promotionUserSaveEvent.eventType())
        .payloads(from(promotionUserSaveEvent.payloads()))
        .userInfo(promotionUserSaveEvent.userInfo())
        .build();
  }

  private static List<PromotionUserSavePayloadInfo> from(List<PromotionUserSavePayload> payloads) {
    return payloads.stream().map(PromotionUserSavePayload::from).toList();
  }
}
