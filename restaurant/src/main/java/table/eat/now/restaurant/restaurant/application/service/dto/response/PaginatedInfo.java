/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 26.
 */
package table.eat.now.restaurant.restaurant.application.service.dto.response;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import table.eat.now.restaurant.restaurant.domain.dto.response.Paginated;

@Builder
public record PaginatedInfo<T>(
    List<T> contents,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

  public static <T> PaginatedInfo<T> from(Page<T> page) {
    Pageable pageable = page.getPageable();

    return PaginatedInfo.<T>builder()
        .contents(page.getContent())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNumber(pageable.getPageNumber() + 1)
        .pageSize(pageable.getPageSize())
        .build();
  }

  public static <T> PaginatedInfo<T> of(
      List<T> contents, long totalElements, int totalPages, int pageNumber, int pageSize) {

    return PaginatedInfo.<T>builder()
        .contents(contents)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();
  }

  public static <T> PaginatedInfo<T> from(Paginated<T> page) {

    return PaginatedInfo.<T>builder()
        .contents(page.contents())
        .totalElements(page.totalElements())
        .totalPages(page.totalPages())
        .pageNumber(page.pageNumber())
        .pageSize(page.pageSize())
        .build();
  }

  public <R> PaginatedInfo<R> mapWithIndex(BiFunction<T, Long, R> mapper) {
    return PaginatedInfo.<R>builder()
        .contents(getListWithIndex(mapper))
        .totalElements(this.totalElements)
        .totalPages(this.totalPages)
        .pageNumber(this.pageNumber)
        .pageSize(this.pageSize)
        .build();
  }

  public long offset() {
    return (long) (this.pageNumber - 1) * this.pageSize + 1;
  }

  private <R> List<R> getListWithIndex(BiFunction<T, Long, R> mapper) {
    //  이렇게도 되는데 뭐가 더 좋을까?
//      long[] idx = { offset() };
//
//        return this.contents.stream()
//            .map(t -> mapper.apply(t, idx[0]++))
//          .toList();
    return IntStream.range(0, contents.size())
        .mapToObj(i -> mapper.apply(contents.get(i), i + this.offset()))
        .toList();
  }
}