package table.eat.now.promotion.promotion.application.event.produce;

import table.eat.now.promotion.promotion.application.event.EventType;
import table.eat.now.promotion.promotion.application.event.PromotionEvent;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 25.
 */
public record PromotionScheduleEvent(EventType eventType,
                                     PromotionSchedulePayload payload
                                     ) implements PromotionEvent {


}
