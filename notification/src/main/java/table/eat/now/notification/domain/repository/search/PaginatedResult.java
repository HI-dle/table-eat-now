package table.eat.now.notification.domain.repository.search;

import java.util.List;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
public record PaginatedResult<T>(List<T> content,
                              int page,
                              int size,
                              Long totalElements,
                              int totalPages) {

}
