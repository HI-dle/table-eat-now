package table.eat.now.promotion.promotion.application.dto;

import java.util.List;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */

public record PaginatedResultCommand<T>(List<T> content,
                                        int page,
                                        int size,
                                        Long totalElements,
                                        int totalPages) {

  public static PaginatedResultCommand<SearchPromotionInfo> from(
      PaginatedResult<PromotionSearchCriteriaQuery> result
  ) {
    List<SearchPromotionInfo> content = result.content().stream()
        .map(SearchPromotionInfo::from)
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
