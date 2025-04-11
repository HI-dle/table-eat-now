package table.eat.now.review.domain.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static table.eat.now.review.domain.entity.ReviewVisibility.HiddenByRole.CUSTOMER;

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

			assertThat(exception.getMessage()).contains("null일 수 없습니다");
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

			assertThat(exception.getMessage()).contains("null일 수 없습니다");
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

			assertThat(exception.getMessage()).contains("null일 수 없습니다");
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

		private Long ownerId;
		private Long otherUserId;
		private String restaurantId;
		private String serviceId;
		private Review review;

		@BeforeEach
		void setUp() {
			ownerId = 123L;
			otherUserId = 456L;
			serviceId = "RESERVATION";
			String role = "CUSTOMER";
			restaurantId = UUID.randomUUID().toString();
			serviceId = UUID.randomUUID().toString();
			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.valueOf(role)
			);
			review = command.toEntity();
		}

		@Test
		void 공개_리뷰는_모든_사용자가_접근_가능하다() {
			// given
			CreateReviewCommand publicCommand = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					true,
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

	@Nested
	class hide_는 {

		private Long ownerId;
		private Long otherUserId;
		private String restaurantId;
		private String serviceId;
		private String role;
		private Review originalReview;

		@BeforeEach
		void setUp() {
			ownerId = 123L;
			otherUserId = 456L;
			serviceId = "RESERVATION";
			role = "CUSTOMER";
			restaurantId = UUID.randomUUID().toString();
			serviceId = UUID.randomUUID().toString();
			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					true,
					UserRole.valueOf(role)
			);
			originalReview = command.toEntity();
		}

		@Test
		void 숨김_상태로_변경할_수_있다() {
			// when
			Review hidden = originalReview.hide(ownerId, role);

			// then
			assertThat(hidden.getVisibility().getHiddenBy()).isEqualTo(ownerId);
			assertThat(hidden.getVisibility().getHiddenByRole()).isEqualTo(CUSTOMER);
			assertThat(hidden.getVisibility().isVisible()).isFalse();
		}

		@Test
		void 일반_유저인_경우_본인의_리뷰가_아니면_IllegalArgumentException_을_던진다() {
			// given
			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.valueOf(role)
			);
			Review review = command.toEntity();

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> review.hide(otherUserId, role));
			assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
		}
	}

	@Nested
	class show_는 {

		private Long ownerId;
		private Long otherUserId;
		private String restaurantId;
		private String serviceId;
		private String role;
		private Review originalReview;

		@BeforeEach
		void setUp() {
			ownerId = 123L;
			otherUserId = 456L;
			serviceId = "RESERVATION";
			role = "CUSTOMER";
			restaurantId = UUID.randomUUID().toString();
			serviceId = UUID.randomUUID().toString();
			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.valueOf(role)
			);
			originalReview = command.toEntity();
		}

		@Test
		void 공개_상태로_변경할_수_있다() {
			// when
			Review shown = originalReview.show(ownerId, role);

			// then
			assertThat(shown.getVisibility().getHiddenBy()).isNull();
			assertThat(shown.getVisibility().getHiddenByRole()).isNull();
			assertThat(shown.getVisibility().isVisible()).isTrue();
		}

		@Test
		void 일반_유저인_경우_본인의_리뷰가_아니면_IllegalArgumentException_을_던진다() {
			// given
			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.valueOf(role)
			);
			Review review = command.toEntity();

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> review.show(otherUserId, role));
			assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
		}
	}

	@Nested
	class update_는 {

		private Long ownerId;
		private Long otherUserId;
		private String role;
		private Review originalReview;
		private String newContent;
		private Integer newRating;
		private ReviewContent newReviewContent;

		@BeforeEach
		void setUp() {
			ownerId = 123L;
			otherUserId = 456L;
			role = "CUSTOMER";
			String restaurantId = UUID.randomUUID().toString();
			String serviceId = UUID.randomUUID().toString();

			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.valueOf(role)
			);
			originalReview = command.toEntity();

			newContent  = "나쁘지않네요";
			newRating = 1;
			newReviewContent = ReviewContent.create(newContent, newRating);
		}

		@Test
		void 본인의_리뷰를_수정할_수_있다() {
			// given
			UpdateContent updateContent = new UpdateContent(newReviewContent, ownerId, role);

			// when
			originalReview.update(updateContent);

			// then
			assertThat(originalReview.getContent()).isEqualTo(newReviewContent);
			assertThat(originalReview.getContent().getRating()).isEqualTo(newRating);
			assertThat(originalReview.getContent().getContent()).isEqualTo(newContent);
		}

		@Test
		void 일반_유저인_경우_본인의_리뷰가_아니면_IllegalArgumentException_을_던진다() {
			// given
			UpdateContent updateContent = new UpdateContent(newReviewContent, otherUserId, role);

			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> originalReview.update(updateContent));
			assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
		}
	}

	@Nested
	class delete_는 {

		private Long ownerId;
		private Long otherUserId;
		private String role;
		private Review originalReview;


		@BeforeEach
		void setUp() {
			ownerId = 123L;
			otherUserId = 456L;
			role = CUSTOMER.name();
			String restaurantId = UUID.randomUUID().toString();
			String serviceId = UUID.randomUUID().toString();

			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, ownerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.valueOf(role)
			);
			originalReview = command.toEntity();
		}

		@Test
		void 본인의_리뷰를_삭제할_수_있다() {
			// when
			originalReview.delete(ownerId, role);

			// then
			assertThat(originalReview.getDeletedAt()).isNotNull();
			assertThat(originalReview.getDeletedBy()).isEqualTo(ownerId);
		}

		@Test
		void 일반_유저인_경우_본인의_리뷰가_아니면_IllegalArgumentException_을_던진다() {
			// when & then
			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class, () -> originalReview.delete(otherUserId, role));
			assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
		}
	}
}