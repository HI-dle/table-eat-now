package table.eat.now.waiting.waiting_request.application.dto.response;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.waiting.waiting_request.domain.info.Paginated;

@Builder
public record PageResult<T>(
    List<T> contents,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static <T> PageResult<T> from(Page<T> page) {
    Pageable pageable = page.getPageable();

    return PageResult.<T>builder()
        .contents(page.getContent())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNumber(pageable.getPageNumber() + 1)
        .pageSize(pageable.getPageSize())
        .build();
  }

  public static <T> PageResult<T> of(
      List<T> contents, long totalElements, int totalPages, int pageNumber, int pageSize) {

    return PageResult.<T>builder()
        .contents(contents)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();
  }

  private static <U, T> PageResult<U> from(PageResult<T> pageResult, List<U> list) {

    return PageResult.<U>builder()
        .contents(list)
        .totalElements(pageResult.totalElements())
        .totalPages(pageResult.totalPages())
        .pageNumber(pageResult.pageNumber())
        .pageSize(pageResult.pageSize())
        .build();
  }

  public static <T> PageResult<T> from(Paginated<T> page) {

    return PageResult.<T>builder()
        .contents(page.contents())
        .totalElements(page.totalElements())
        .totalPages(page.totalPages())
        .pageNumber(page.pageNumber())
        .build();
  }

  public <U> PageResult<U> map(Function<T, U> mapper) {

    return PageResult.from(this, this.contents.<T>stream()
        .map(t -> mapper.apply(t))
        .toList());
  }

  public <U> PageResult<U> mapWithIndex(long offset, BiFunction<T, Long, U> mapper) {

    AtomicLong idx = new AtomicLong(offset);
    return PageResult.from(this, this.contents.stream()
        .map(t -> mapper.apply(t, idx.getAndIncrement()))
        .toList());
  }
}