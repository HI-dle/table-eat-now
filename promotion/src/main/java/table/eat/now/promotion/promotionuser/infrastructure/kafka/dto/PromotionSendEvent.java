package table.eat.now.promotion.promotionuser.infrastructure.kafka.dto;


import lombok.Builder;
import table.eat.now.promotion.promotionuser.application.event.EventType;
import table.eat.now.promotion.promotionuser.application.event.PromotionEvent;

@Builder
public record PromotionSendEvent(
    EventType eventType,
    PromotionSendPayload payload
) implements PromotionEvent {

  public static PromotionSendEvent from(PromotionSendPayload payload) {
    return PromotionSendEvent.builder()
        .eventType(EventType.SEND)
        .payload(payload)
        .build();
  }

}
