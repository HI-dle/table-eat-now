package table.eat.now.promotion.promotion.application.event;

import table.eat.now.promotion.promotion.application.event.produce.PromotionUserCouponSaveEvent;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
public interface PromotionEventPublisher {

  void publish(PromotionEvent event);
  void publish(PromotionUserCouponSaveEvent event);

}
