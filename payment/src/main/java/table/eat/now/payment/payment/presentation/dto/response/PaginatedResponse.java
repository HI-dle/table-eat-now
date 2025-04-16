package table.eat.now.payment.payment.presentation.dto.response;

import java.util.List;
import java.util.function.Function;
import table.eat.now.payment.payment.application.dto.response.PaginatedInfo;

public record PaginatedResponse<T>(
    List<T> content,
    int page,
    int size,
    Long totalElements,
    int totalPages) {

  public static <T>PaginatedResponse<T> from(PaginatedInfo<T> info) {

    return new PaginatedResponse<T>(
        info.content(),
        info.page(),
        info.size(),
        info.totalElements(),
        info.totalPages()
    );
  }

  public <U> PaginatedResponse<U> map(Function<T, U> mapper) {

    return PaginatedResponse
        .from(this, this.content.stream()
        .map(mapper)
        .toList());
  }

  private static <T, U> PaginatedResponse<U> from(
      PaginatedResponse<T> response,
      List<U> content) {

    return new PaginatedResponse<>(
        content,
        response.page(),
        response.size(),
        response.totalElements(),
        response.totalPages()
    );
  }

}
