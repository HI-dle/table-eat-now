package table.eat.now.promotion.promotionUser.domain.repository.search;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record PromotionUserSearchCriteria(String promotionUuid,
                                          Long userId,
                                          Boolean isAsc,
                                          String sortBy,
                                          int page,
                                          int size) {

}
