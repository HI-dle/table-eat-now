package table.eat.now.review.application.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.review.domain.repository.search.CursorResult;

class CursorTest {

  @Test
  void empty_메서드는_null_값을_가진_커서를_반환할_수_있다() {
    // when
    Cursor emptyCursor = Cursor.empty();

    // then
    assertThat(emptyCursor.lastProcessedUpdatedAt()).isNull();
    assertThat(emptyCursor.lastProcessedRestaurantId()).isNull();
  }

  @Test
  void of_메서드는_지정된_값으로_커서를_생성할_수_있다() {
    // given
    LocalDateTime time = LocalDateTime.of(2025, 4, 27, 12, 0);
    String restaurantId = "restaurant-1";

    // when
    Cursor cursor = Cursor.of(time, restaurantId);

    // then
    assertThat(cursor.lastProcessedUpdatedAt()).isEqualTo(time);
    assertThat(cursor.lastProcessedRestaurantId()).isEqualTo(restaurantId);
  }

  @Test
  void from_메서드는_CursorResult로부터_커서를_생성할_수_있다() {
    // given
    LocalDateTime time = LocalDateTime.of(2025, 4, 27, 12, 0);
    String restaurantId = "restaurant-1";
    CursorResult cursorResult = new CursorResult(time, restaurantId);

    // when
    Cursor cursor = Cursor.from(cursorResult);

    // then
    assertThat(cursor.lastProcessedUpdatedAt()).isEqualTo(time);
    assertThat(cursor.lastProcessedRestaurantId()).isEqualTo(restaurantId);
  }

  @Nested
  class compareTo_메서드는 {

    @Test
    void 시간이_다르면_시간_기준으로_비교한다() {
      // given
      LocalDateTime earlier = LocalDateTime.of(2025, 4, 27, 10, 0);
      LocalDateTime later = LocalDateTime.of(2025, 4, 27, 12, 0);

      Cursor earlierCursor = Cursor.of(earlier, "restaurant-1");
      Cursor laterCursor = Cursor.of(later, "restaurant-1");

      // then
      assertThat(earlierCursor.compareTo(laterCursor)).isNegative();
      assertThat(laterCursor.compareTo(earlierCursor)).isPositive();
    }

    @Test
    void 시간이_같으면_레스토랑_ID로_비교한다() {
      // given
      LocalDateTime sameTime = LocalDateTime.of(2025, 4, 27, 12, 0);

      Cursor cursor1 = Cursor.of(sameTime, "restaurant-1");
      Cursor cursor2 = Cursor.of(sameTime, "restaurant-2");

      // then
      assertThat(cursor1.compareTo(cursor2)).isNegative();
      assertThat(cursor2.compareTo(cursor1)).isPositive();
    }

    @Test
    void 시간과_ID가_모두_같으면_0을_반환한다() {
      // given
      LocalDateTime sameTime = LocalDateTime.of(2025, 4, 27, 12, 0);
      String sameId = "restaurant-1";

      Cursor cursor1 = Cursor.of(sameTime, sameId);
      Cursor cursor2 = Cursor.of(sameTime, sameId);

      // then
      assertThat(cursor1.compareTo(cursor2)).isZero();
    }

    @Test
    void 시간_null은_non_null보다_작다고_간주한다() {
      // given
      Cursor nullTimeCursor = Cursor.of(null, "restaurant-1");
      Cursor nonNullTimeCursor = Cursor.of(LocalDateTime.now(), "restaurant-1");

      // then
      assertThat(nullTimeCursor.compareTo(nonNullTimeCursor)).isNegative();
      assertThat(nonNullTimeCursor.compareTo(nullTimeCursor)).isPositive();
    }

    @Test
    void 레스토랑_ID_null은_non_null보다_작다고_간주한다() {
      // given
      LocalDateTime sameTime = LocalDateTime.of(2025, 4, 27, 12, 0);

      Cursor nullIdCursor = Cursor.of(sameTime, null);
      Cursor nonNullIdCursor = Cursor.of(sameTime, "restaurant-1");

      // then
      assertThat(nullIdCursor.compareTo(nonNullIdCursor)).isNegative();
      assertThat(nonNullIdCursor.compareTo(nullIdCursor)).isPositive();
    }

    @Test
    void empty_커서는_다른_커서보다_작다고_간주한다() {
      // given
      Cursor emptyCursor = Cursor.empty();
      Cursor nonEmptyCursor = Cursor.of(LocalDateTime.now(), "restaurant-1");

      // then
      assertThat(emptyCursor.compareTo(nonEmptyCursor)).isNegative();
      assertThat(nonEmptyCursor.compareTo(emptyCursor)).isPositive();
    }
  }
}
