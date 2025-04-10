package table.eat.now.promotion.promotionRestaurant.application.dto;

import java.util.List;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.SearchPromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteria;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteriaQuery;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */

public record PaginatedResultCommand<T>(List<T> content,
                                        int page,
                                        int size,
                                        Long totalElements,
                                        int totalPages) {

  public static PaginatedResultCommand<SearchPromotionRestaurantInfo> from(
      PaginatedResult<PromotionRestaurantSearchCriteriaQuery> result
  ) {
    List<SearchPromotionRestaurantInfo> content = result.content().stream()
        .map(SearchPromotionRestaurantInfo::from)
        .toList();

    return new PaginatedResultCommand<>(
        content,
        result.page(),
        result.size(),
        result.totalElements(),
        result.totalPages()
    );
  }

}
