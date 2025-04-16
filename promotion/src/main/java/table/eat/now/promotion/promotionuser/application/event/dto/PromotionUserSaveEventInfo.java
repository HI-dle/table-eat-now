package table.eat.now.promotion.promotionuser.application.event.dto;



import java.util.List;
import lombok.Builder;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionuser.application.event.EventType;
import table.eat.now.promotion.promotionuser.application.event.PromotionEvent;
import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;

@Builder
public record PromotionUserSaveEventInfo(
    EventType eventType,
    List<PromotionUserSavePayloadInfo> payloads,
    CurrentUserInfoDto userInfo
) implements PromotionEvent {
  public static List<PromotionUser> from(List<PromotionUserSavePayloadInfo> payloads) {
    return payloads.stream()
        .map(payload -> PromotionUser.of(payload.userId(), payload.promotionUuid()))
        .toList();
  }
}
