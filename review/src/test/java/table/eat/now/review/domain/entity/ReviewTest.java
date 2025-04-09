package table.eat.now.review.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;

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
		void 생성된_Review는_UUID_형식의_문자열을_갖는다() {
			// given
			ReviewReference validReference = mock(ReviewReference.class);
			ReviewContent validContent = mock(ReviewContent.class);
			ReviewVisibility validVisibility = mock(ReviewVisibility.class);

			// when
			Review review = Review.create(validReference, validContent, validVisibility);

			// then
			assertThat(review.getReviewId()).isNotNull();
			assertThat(review.getReviewId()).isInstanceOf(String.class);
			assertThat(UUID.fromString(review.getReviewId())).isNotNull();
		}
	}

	@Nested
	class isAccessible_은 {

		private Review review;
		private Long ownerId = 123L;
		private Long otherUserId = 456L;
		private String restaurantId = UUID.randomUUID().toString();
		private String serviceId = UUID.randomUUID().toString();

		@BeforeEach
		void setUp() {
			// CreateReviewCommand를 사용하여 Review 객체 생성
			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false, // 기본적으로 비공개로 설정
					UserRole.CUSTOMER
			);

			review = command.toEntity();
		}

		@Test
		void 공개_리뷰는_모든_사용자가_접근_가능하다() {
			// given
			// 공개 리뷰 생성
			CreateReviewCommand publicCommand = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					true, // 공개로 설정
					UserRole.CUSTOMER
			);
			Review publicReview = publicCommand.toEntity();

			// when & then
			assertThat(publicReview.isAccessible(otherUserId, "CUSTOMER")).isTrue();
		}

		@Test
		void 일반_사용자_중_비공개_리뷰는_작성자만_접근_가능하다() {
			// when & then
			assertThat(review.isAccessible(ownerId, "CUSTOMER")).isTrue();
			assertThat(review.isAccessible(otherUserId, "CUSTOMER")).isFalse();
		}

		@Test
		void MASTER_역할은_공개_여부에_상관_없이_모든_리뷰에_접근_가능하다() {
			// when & then
			assertThat(review.isAccessible(otherUserId, "MASTER")).isTrue();
		}

		@Test
		void 리뷰_작성자는_항상_자신의_리뷰에_접근_가능하다() {
			// when & then
			assertThat(review.isAccessible(ownerId, "CUSTOMER")).isTrue();
		}
	}
}