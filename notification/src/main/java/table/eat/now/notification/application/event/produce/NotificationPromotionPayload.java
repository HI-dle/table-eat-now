package table.eat.now.notification.application.event.produce;

import lombok.Builder;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 28.
 */
@Builder
public record NotificationPromotionPayload(String promotionUuid,
                                           Long userId) {

}
