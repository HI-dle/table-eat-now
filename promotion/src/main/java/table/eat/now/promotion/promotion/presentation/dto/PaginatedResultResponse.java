package table.eat.now.promotion.promotion.presentation.dto;

import java.util.List;
import table.eat.now.promotion.promotion.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;
import table.eat.now.promotion.promotion.presentation.dto.response.SearchPromotionResponse;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */
public record PaginatedResultResponse<T>(List<T> content,
                                         int page,
                                         int size,
                                         Long totalElements,
                                         int totalPages) {

  public static PaginatedResultResponse<SearchPromotionResponse> from(
      PaginatedResultCommand<SearchPromotionInfo> result
  ) {
    List<SearchPromotionResponse> content = result.content().stream()
        .map(SearchPromotionResponse::from)
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
