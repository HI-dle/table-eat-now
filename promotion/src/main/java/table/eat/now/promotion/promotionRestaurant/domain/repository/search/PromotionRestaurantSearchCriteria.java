package table.eat.now.promotion.promotionRestaurant.domain.repository.search;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record PromotionRestaurantSearchCriteria(String promotionUuid,
                                                String restaurantUuid,
                                                Boolean isAsc,
                                                String sortBy,
                                                int page,
                                                int size) {

}
