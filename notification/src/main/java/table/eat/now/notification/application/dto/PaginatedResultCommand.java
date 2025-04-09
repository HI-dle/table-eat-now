package table.eat.now.notification.application.dto;

import java.util.List;


/**
 * @author : hanjihoon
 * @Date : 2025. 03. 17.
 */
public record PaginatedResultCommand<T>(List<T> content,
                                        int page,
                                        int size,
                                        Long totalElements,
                                        int totalPages) {

}
