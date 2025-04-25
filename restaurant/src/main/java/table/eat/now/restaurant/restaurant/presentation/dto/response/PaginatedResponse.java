package table.eat.now.restaurant.restaurant.presentation.dto.response;

import java.util.List;
import java.util.function.Function;
import table.eat.now.restaurant.restaurant.application.service.dto.response.PaginatedInfo;

public record PaginatedResponse<T>(
    List<T> contents,
    int pageNumber,
    int pageSize,
    Long totalElements,
    int totalPages) {

  public static <T>PaginatedResponse<T> from(PaginatedInfo<T> info) {

    return new PaginatedResponse<T>(
        info.contents(),
        info.pageNumber(),
        info.pageSize(),
        info.totalElements(),
        info.totalPages()
    );
  }

  public <U> PaginatedResponse<U> map(Function<T, U> mapper) {

    return PaginatedResponse
        .from(this, this.contents.stream()
            .map(mapper)
            .toList());
  }

  private static <T, U> PaginatedResponse<U> from(
      PaginatedResponse<T> response,
      List<U> content) {

    return new PaginatedResponse<>(
        content,
        response.pageNumber(),
        response.pageSize(),
        response.totalElements(),
        response.totalPages()
    );
  }

}
