package table.eat.now.review.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewContentTest {

	@Nested
	class create_는 {

		@Test
		void 유효한_내용과_평점으로_ReviewContent_를_생성_할_수_있다() {
			//given
			String validContent = "음식이 맛있고 사장님이 친절해요";
			Integer validRating = 3;

			//when & then
			ReviewContent reviewContent = assertDoesNotThrow(
					() -> ReviewContent.create(validContent, validRating));

			assertThat(reviewContent).isNotNull();
			assertThat(reviewContent.getContent()).isEqualTo(validContent);
			assertThat(reviewContent.getRating()).isEqualTo(validRating);
		}

		@Test
		void 내용이_null_이면_IllegalArgumentException_을_던진다() {
			//given
			Integer validRating = 3;

			//when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> ReviewContent.create(null, validRating));

			assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
		}

		@Test
		void 평점이_null_이면_IllegalArgumentException_을_던진다() {
			//given
			String validContent = "음식이 맛있고 사장님이 친절해요";

			//when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> ReviewContent.create(validContent, null));

			assertThat(exception.getMessage()).contains("null이 될 수 없습니다");
		}

		@Test
		void 내용이_빈_값_이면_IllegalArgumentException_을_던진다() {
			// given
			String emptyContent = "";
			Integer validRating = 4;

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> ReviewContent.create(emptyContent, validRating));

			assertThat(exception.getMessage()).contains("비어있을 수 없습니다");
		}

		@Test
		void 내용이_공백_이면_IllegalArgumentException_을_던진다() {
			// given
			String emptyContent = "   ";
			Integer validRating = 4;

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> ReviewContent.create(emptyContent, validRating));

			assertThat(exception.getMessage()).contains("비어있을 수 없습니다");
		}

		@ValueSource(ints = {-1, 6})
		@ParameterizedTest(name = "[{index}] 유효하지 않은 경계값 평점: {0}")
		@DisplayName("평점이_유효하지_않으면_IllegalArgumentException_을_던진다")
		void 유효하지_않은_경계값_테스트 (int invalidRating) {
			// given
			String validContent = "음식이 맛있고 사장님이 친절해요";

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> ReviewContent.create(validContent, invalidRating));

			assertThat(exception.getMessage()).contains("0에서 5 사이");
		}

		@ValueSource(ints = {0, 5})
		@ParameterizedTest(name = "[{index}] 경계값 평점: {0}")
		@DisplayName("유효한_평점으로_ReviewContent_를_생성할_수_있다")
		void 유효한_경계값_테스트(int validRating) {
			// given
			String validContent = "음식이 맛있고 사장님이 친절해요";

			// when & then
			assertDoesNotThrow(() -> ReviewContent.create(validContent, validRating));
		}
	}
}