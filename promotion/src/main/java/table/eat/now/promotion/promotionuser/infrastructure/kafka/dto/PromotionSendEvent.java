package table.eat.now.promotion.promotionuser.infrastructure.kafka.dto;


import lombok.Builder;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;

@Builder
public record PromotionSendEvent(
    EventType eventType,
    PromotionSendPayload payload
) implements PromotionEvent {

  public static PromotionSendEvent from(PromotionSendPayload payload) {
    return PromotionSendEvent.builder()
        .eventType(EventType.PROMOTION_SEND)
        .payload(payload)
        .build();
  }

}
