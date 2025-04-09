package table.eat.now.notification.presentation.dto;

import java.util.List;
import table.eat.now.notification.application.dto.PaginatedResultCommand;
import table.eat.now.notification.application.dto.response.NotificationSearchInfo;
import table.eat.now.notification.presentation.dto.response.NotificationSearchResponse;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */
public record PaginatedResultResponse<T>(List<T> content,
                                         int page,
                                         int size,
                                         Long totalElements,
                                         int totalPages) {

  public static PaginatedResultResponse<NotificationSearchResponse> from(
      PaginatedResultCommand<NotificationSearchInfo> result
  ) {
    List<NotificationSearchResponse> content = result.content().stream()
        .map(NotificationSearchResponse::from)
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
