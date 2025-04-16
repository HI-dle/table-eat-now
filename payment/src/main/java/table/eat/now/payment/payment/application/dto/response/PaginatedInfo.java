package table.eat.now.payment.payment.application.dto.response;

import java.util.List;
import java.util.function.Function;
import table.eat.now.payment.payment.domain.repository.search.PaginatedResult;

public record PaginatedInfo<T>(
    List<T> content,
    int page,
    int size,
    Long totalElements,
    int totalPages) {

  public static <T> PaginatedInfo<T> from(PaginatedResult<T> result) {
    return new PaginatedInfo<>(
        result.content(),
        result.page(),
        result.size(),
        result.totalElements(),
        result.totalPages()
    );
  }

  public <U> PaginatedInfo<U> map(Function<T, U> mapper) {
    return PaginatedInfo.from(
        this,
        this.content.stream()
            .map(mapper)
            .toList()
    );
  }

  private static <T, U> PaginatedInfo<U> from(
      PaginatedInfo<T> info,
      List<U> content) {

    return new PaginatedInfo<>(
        content,
        info.page(),
        info.size(),
        info.totalElements(),
        info.totalPages()
    );
  }
}