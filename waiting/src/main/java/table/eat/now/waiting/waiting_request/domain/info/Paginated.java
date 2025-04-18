package table.eat.now.waiting.waiting_request.domain.info;

import java.util.List;
import lombok.Builder;

@Builder
public record Paginated<T>(
    List<T> contents,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static <T> Paginated<T> of(
      List<T> contents, long totalElements, int totalPages, int pageNumber, int pageSize) {
    return Paginated.<T>builder()
        .contents(contents)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();
  }
}
