package table.eat.now.promotion.promotionuser.domain.repository.search;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record PromotionUserSearchCriteria(Long userId,
                                          String promotionUuid,
                                          Boolean isAsc,
                                          String sortBy,
                                          int page,
                                          int size) {

}
