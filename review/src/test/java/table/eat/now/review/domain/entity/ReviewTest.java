package table.eat.now.review.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewTest {

	@Nested
	class create_는 {

		@Test
		void 유효한_파라미터로_Review를_생성할_수_있다() {
			// given
			ReviewReference validReference = mock(ReviewReference.class);
			ReviewContent validContent = mock(ReviewContent.class);
			ReviewVisibility validVisibility = mock(ReviewVisibility.class);

			// when & then
			Review review = assertDoesNotThrow(() -> Review.create(
					validReference, validContent, validVisibility));

			assertThat(review).isNotNull();
			assertThat(review.getReviewId()).isNotNull();
			assertThat(review.getReference()).isEqualTo(validReference);
			assertThat(review.getContent()).isEqualTo(validContent);
			assertThat(review.getVisibility()).isEqualTo(validVisibility);
		}

		@Test
		void reference가_null_이면_IllegalArgumentException_을_던진다() {
			// given
			ReviewContent validContent = mock(ReviewContent.class);
			ReviewVisibility validVisibility = mock(ReviewVisibility.class);

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> Review.create(
							null, validContent, validVisibility));

			assertThat(exception.getMessage()).contains("ReviewReference는 null일 수 없습니다");
		}

		@Test
		void content가_null이면_IllegalArgumentException_을_던진다() {
			// given
			ReviewReference validReference = mock(ReviewReference.class);
			ReviewVisibility validVisibility = mock(ReviewVisibility.class);

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> Review.create(
							validReference, null, validVisibility));

			assertThat(exception.getMessage()).contains("ReviewContent는 null일 수 없습니다");
		}

		@Test
		void visibility가_null이면_IllegalArgumentException_을_던진다() {
			// given
			ReviewReference validReference = mock(ReviewReference.class);
			ReviewContent validContent = mock(ReviewContent.class);

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> Review.create(
							validReference, validContent, null));

			assertThat(exception.getMessage()).contains("ReviewVisibility는 null일 수 없습니다");
		}

		@Test
		void 생성된_Review는_UUID를_갖는다() {
			// given
			ReviewReference validReference = mock(ReviewReference.class);
			ReviewContent validContent = mock(ReviewContent.class);
			ReviewVisibility validVisibility = mock(ReviewVisibility.class);

			// when
			Review review = Review.create(validReference, validContent, validVisibility);

			// then
			assertThat(review.getReviewId()).isNotNull();
			assertThat(review.getReviewId()).isInstanceOf(UUID.class);
		}
	}
}