package table.eat.now.review.domain.repository.search;

import java.time.LocalDateTime;

public record CursorResult(
    LocalDateTime updatedAt,
    String restaurantId
) {

}
