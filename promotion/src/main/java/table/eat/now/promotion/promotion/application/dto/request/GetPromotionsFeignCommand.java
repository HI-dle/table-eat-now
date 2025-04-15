package table.eat.now.promotion.promotion.application.dto.request;

import java.util.Set;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 13.
 */
public record GetPromotionsFeignCommand(Set<String> promotionUuids,
                                        String restaurantUuid) {


}
