package table.eat.now.review.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewVisibilityTest {

  @Nested
  class create_는 {

    @Test
    void 공개_상태로_생성할_수_있다() {
      // given
      Boolean isVisible = true;

      // when
      ReviewVisibility result = ReviewVisibility.create(isVisible, null, null);

      // then
      assertThat(result.isVisible()).isTrue();
      assertThat(result.getHiddenBy()).isNull();
      assertThat(result.getHiddenAt()).isNull();
      assertThat(result.getHiddenByRole()).isNull();
    }

    @Test
    void 숨김_상태로_생성할_수_있다() {
      // given
      Boolean isVisible = false;
      Long userId = 1L;
      String userRole = "CUSTOMER";

      // when
      ReviewVisibility result = ReviewVisibility.create(isVisible, userId, userRole);

      // then
      assertThat(result.isVisible()).isFalse();
      assertThat(result.getHiddenBy()).isEqualTo(userId);
      assertThat(result.getHiddenAt()).isNotNull();
      assertThat(result.getHiddenByRole().name()).isEqualTo("CUSTOMER");
    }

    @Test
    void isVisible이_null이면_예외가_발생한다() {
      // expect
      assertThrows(IllegalArgumentException.class,
          () -> ReviewVisibility.create(null, null, null));
    }

    @Test
    void 숨김일_경우_userId나_role이_null이면_예외가_발생한다() {
      // expect
      assertThrows(IllegalArgumentException.class,
          () -> ReviewVisibility.create(false, null, "CUSTOMER"));

      assertThrows(IllegalArgumentException.class,
          () -> ReviewVisibility.create(false, 1L, null));
    }

    @Test
    void 유효하지_않은_권한값이면_예외가_발생한다() {
      // expect
      assertThrows(IllegalArgumentException.class,
          () -> ReviewVisibility.create(false, 1L, "INVALID_ROLE"));
    }
  }

  @Nested
  class hide_는 {

    @Test
    void 공개된_리뷰를_숨길_수_있다() {
      // given
      ReviewVisibility visibility = ReviewVisibility.create(
          true, null, null);
      Long userId = 2L;
      String role = "STAFF";

      // when
      ReviewVisibility hidden = visibility.hide(userId, role);

      // then
      assertThat(hidden.isVisible()).isFalse();
      assertThat(hidden.getHiddenBy()).isEqualTo(userId);
      assertThat(hidden.getHiddenAt()).isNotNull();
      assertThat(hidden.getHiddenByRole().name()).isEqualTo("STAFF");
    }

    @Test
    void 이미_숨겨진_리뷰는_변경되지_않는다() {
      // given
      ReviewVisibility hidden = ReviewVisibility.create(
          false, 2L, "OWNER");

      // when
      ReviewVisibility result = hidden.hide(3L, "CUSTOMER");

      // then
      assertThat(result).isEqualTo(hidden);
    }
  }

  @Nested
  class show_는 {

    @Test
    void 일반_유저가_일반_유저가_숨긴_리뷰를_공개할_수_있다() {
      // given
      ReviewVisibility hidden = ReviewVisibility.create(
          false, 3L, "CUSTOMER");

      // when
      ReviewVisibility result = hidden.show("CUSTOMER");

      // then
      assertThat(result.isVisible()).isTrue();
      assertThat(result.getHiddenBy()).isNull();
    }

    @Test
    void 관리자가_숨긴_리뷰를_일반_유저가_공개하면_예외가_발생한다() {
      // given
      ReviewVisibility hidden = ReviewVisibility.create(
          false, 1L, "STAFF");

      // expect
      assertThrows(IllegalArgumentException.class, () -> hidden.show("CUSTOMER"));
    }

    @Test
    void 관리자가_숨긴_리뷰는_관리자가_공개할_수_있다() {
      // given
      ReviewVisibility hidden = ReviewVisibility.create(
          false, 1L, "MASTER");

      // when
      ReviewVisibility result = hidden.show("STAFF");

      // then
      assertThat(result.isVisible()).isTrue();
      assertThat(result.getHiddenBy()).isNull();
    }

    @Test
    void 이미_공개된_리뷰는_변경되지_않는다() {
      // given
      ReviewVisibility visible = ReviewVisibility.create(
          true, null, null);

      // when
      ReviewVisibility result = visible.show("CUSTOMER");

      // then
      assertThat(result).isEqualTo(visible);
    }
  }
}
