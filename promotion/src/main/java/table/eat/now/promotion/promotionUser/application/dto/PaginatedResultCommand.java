package table.eat.now.promotion.promotionUser.application.dto;

import java.util.List;
import table.eat.now.promotion.promotionUser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionUser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteriaQuery;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */

public record PaginatedResultCommand<T>(List<T> content,
                                        int page,
                                        int size,
                                        Long totalElements,
                                        int totalPages) {

  public static PaginatedResultCommand<SearchPromotionUserInfo> from(
      PaginatedResult<PromotionUserSearchCriteriaQuery> result
  ) {
    List<SearchPromotionUserInfo> content = result.content().stream()
        .map(SearchPromotionUserInfo::from)
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
