package table.eat.now.promotion.promotionrestaurant.presentation.dto;

import java.util.List;
import table.eat.now.promotion.promotionrestaurant.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.SearchPromotionRestaurantInfo;
import table.eat.now.promotion.promotionrestaurant.presentation.dto.response.SearchPromotionRestaurantResponse;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */
public record PaginatedResultResponse<T>(List<T> content,
                                         int page,
                                         int size,
                                         Long totalElements,
                                         int totalPages) {

  public static PaginatedResultResponse<SearchPromotionRestaurantResponse> from(
      PaginatedResultCommand<SearchPromotionRestaurantInfo> result
  ) {
    List<SearchPromotionRestaurantResponse> content = result.content().stream()
        .map(SearchPromotionRestaurantResponse::from)
        .toList();

    return new PaginatedResultResponse<>(
        content,
        result.page(),
        result.size(),
        result.totalElements(),
        result.totalPages()
    );
  }

}
