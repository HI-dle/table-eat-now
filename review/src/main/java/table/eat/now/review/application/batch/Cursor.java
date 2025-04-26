package table.eat.now.review.application.batch;

import java.time.LocalDateTime;
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
    int timeCompare = compareNullable(this.lastProcessedUpdatedAt, other.lastProcessedUpdatedAt);
    if (timeCompare != 0) {
      return timeCompare;
    }

    return compareNullable(this.lastProcessedRestaurantId, other.lastProcessedRestaurantId);
  }

  private static <T extends Comparable<T>> int compareNullable(T a, T b) {
    if (a == null && b == null) {
      return 0;
    }
    if (a == null) {
      return -1;
    }
    if (b == null) {
      return 1;
    }
    return a.compareTo(b);
  }

}
