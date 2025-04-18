package table.eat.now.review.application.service.dto.response;

import java.util.List;
import table.eat.now.review.domain.repository.search.PaginatedResult;
import table.eat.now.review.domain.repository.search.SearchAdminReviewResult;
import table.eat.now.review.domain.repository.search.SearchReviewResult;

public record PaginatedInfo<T>(
    List<T> content,
    int page,
    int size,
    Long totalElements,
    int totalPages) {

  public static PaginatedInfo<SearchReviewInfo> fromResult(
      PaginatedResult<SearchReviewResult> result) {

    return new PaginatedInfo<>(
        result.content().stream()
            .map(SearchReviewInfo::from)
            .toList(),
        result.page(),
        result.size(),
        result.totalElements(),
        result.totalPages()
    );
  }

  public static PaginatedInfo<SearchAdminReviewInfo> fromAdminResult(
      PaginatedResult<SearchAdminReviewResult> result) {

    return new PaginatedInfo<>(
        result.content().stream()
            .map(SearchAdminReviewInfo::from)
            .toList(),
        result.page(),
        result.size(),
        result.totalElements(),
        result.totalPages()
    );
  }
}
