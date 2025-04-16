package table.eat.now.promotion.promotion.application.event;

import table.eat.now.promotion.promotion.application.event.produce.PromotionUserSaveEvent;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
public interface PromotionEventPublisher {

  void publish(PromotionUserSaveEvent userSaveEvent);

}
