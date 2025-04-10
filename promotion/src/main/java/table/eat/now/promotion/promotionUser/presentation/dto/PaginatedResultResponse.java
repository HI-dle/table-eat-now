package table.eat.now.promotion.promotionUser.presentation.dto;

import java.util.List;
import table.eat.now.promotion.promotionUser.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionUser.presentation.dto.response.SearchPromotionUserResponse;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */
public record PaginatedResultResponse<T>(List<T> content,
                                         int page,
                                         int size,
                                         Long totalElements,
                                         int totalPages) {

  public static PaginatedResultResponse<SearchPromotionUserResponse> from(
      PaginatedResultCommand<SearchPromotionUserInfo> result
  ) {
    List<SearchPromotionUserResponse> content = result.content().stream()
        .map(SearchPromotionUserResponse::from)
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
