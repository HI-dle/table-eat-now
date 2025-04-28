package table.eat.now.review.application.batch;

import java.time.LocalDateTime;
import java.util.Comparator;
import table.eat.now.review.domain.repository.search.CursorResult;

public record Cursor(
    LocalDateTime lastProcessedUpdatedAt,
    String lastProcessedRestaurantId
) implements Comparable<Cursor> {

  public static Cursor empty() {
    return new Cursor(null, null);
  }

  public static Cursor of(LocalDateTime lastProcessedUpdatedAt, String lastProcessedRestaurantId) {
    return new Cursor(lastProcessedUpdatedAt, lastProcessedRestaurantId);
  }

  public static Cursor from(CursorResult endCursorResult) {
    return new Cursor(endCursorResult.updatedAt(), endCursorResult.restaurantId());
  }

  @Override
  public int compareTo(Cursor other) {
    int timeCompare = Comparator
        .<LocalDateTime>nullsFirst(Comparator.naturalOrder())
        .compare(this.lastProcessedUpdatedAt, other.lastProcessedUpdatedAt);

    if (timeCompare != 0) {
      return timeCompare;
    }
    return Comparator
        .<String>nullsFirst(Comparator.naturalOrder())
        .compare(this.lastProcessedRestaurantId, other.lastProcessedRestaurantId);
  }

}
