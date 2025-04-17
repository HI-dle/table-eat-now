package table.eat.now.promotion.promotion.application.event;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 16.
 */
public interface PromotionEventPublisher {

  void publish(PromotionEvent event);

}
