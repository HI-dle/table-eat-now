package table.eat.now.promotion.promotion.application.event.produce;

import lombok.Builder;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 25.
 */
@Builder
public record PromotionScheduleEvent(EventType eventType,
                                     PromotionSchedulePayload payload
                                     ) implements PromotionEvent {

  public static PromotionScheduleEvent from(Promotion promotion) {
    return PromotionScheduleEvent.builder()
        .eventType(determineEventType(promotion.getPromotionStatus()))
        .payload(PromotionSchedulePayload.from(promotion))
        .build();
  }
  private static EventType determineEventType(PromotionStatus status) {
    return switch (status) {
      case READY -> EventType.START;
      case RUNNING -> EventType.END;
      default -> throw CustomException.from(PromotionErrorCode.WRONG_PROMOTION_STATUS);
    };
  }

}
