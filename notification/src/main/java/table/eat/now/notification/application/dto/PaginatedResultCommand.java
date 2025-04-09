package table.eat.now.notification.application.dto;

import java.util.List;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;
import table.eat.now.notification.domain.repository.search.NotificationSearchCriteriaQuery;
import table.eat.now.notification.domain.repository.search.PaginatedResult;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */

public record PaginatedResultCommand<T>(List<T> content,
                                        int page,
                                        int size,
                                        Long totalElements,
                                        int totalPages) {

  public static PaginatedResultCommand<NotificationSearchInfo> from(
      PaginatedResult<NotificationSearchCriteriaQuery> result
  ) {
    List<NotificationSearchInfo> content = result.content().stream()
        .map(NotificationSearchInfo::from)
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
