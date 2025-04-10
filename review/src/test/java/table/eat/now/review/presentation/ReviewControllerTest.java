package table.eat.now.review.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_IS_INVISIBLE;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.config.WebConfig;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.application.service.ReviewService;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;
import table.eat.now.review.presentation.dto.request.CreateReviewRequest;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({
		WebConfig.class,
		CustomPageableArgumentResolver.class,
		CurrentUserInfoResolver.class,
		GlobalErrorHandler.class
})
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ReviewService reviewService;

	@Nested
	class 리뷰_생성시 {

		private String restaurantId;
		private String serviceId;
		private String reviewId;
		private CurrentUserInfoDto userInfo;
		private CreateReviewInfo reviewInfo;


		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID().toString();
			serviceId = UUID.randomUUID().toString();
			reviewId = UUID.randomUUID().toString();
			userInfo = new CurrentUserInfoDto(123L, UserRole.CUSTOMER);

			reviewInfo = CreateReviewInfo.builder()
					.reviewUuid(reviewId)
					.customerId(userInfo.userId())
					.restaurantId(restaurantId)
					.serviceId(serviceId)
					.serviceType("RESERVATION")
					.rating(4)
					.content("맛있는 식당이었습니다.")
					.isVisible(true)
					.createdAt(LocalDateTime.now())
					.build();
		}

		@Test
		void 유효한_요청으로_리뷰를_생성하면_201_상태_코드와_생성된_리뷰_정보를_반환한다() throws Exception {
			// given
			CreateReviewRequest request = new CreateReviewRequest(
					"RESERVATION", restaurantId, serviceId, 4, "맛있는 식당이었습니다.", true);

			when(reviewService.createReview(any(CreateReviewCommand.class))).thenReturn(reviewInfo);

			// when
			ResultActions actions = mockMvc.perform(post("/api/v1/reviews")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(USER_ID_HEADER, "1")
					.header(USER_ROLE_HEADER, "CUSTOMER"));

			// then
			actions
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.reviewUuid").value(reviewId))
					.andExpect(jsonPath("$.customerId").value(userInfo.userId()))
					.andExpect(jsonPath("$.restaurantId").value(restaurantId))
					.andExpect(jsonPath("$.serviceId").value(serviceId))
					.andExpect(jsonPath("$.serviceType").value("RESERVATION"))
					.andExpect(jsonPath("$.rating").value(4))
					.andExpect(jsonPath("$.content").value("맛있는 식당이었습니다."))
					.andExpect(jsonPath("$.isVisible").value(true))
			;

			verify(reviewService).createReview(any(CreateReviewCommand.class));

		}

		@Test
		void serviceType이_유효하지_않으면_400_상태_코드를_반환한다() throws Exception {
			// given
			CreateReviewRequest request = new CreateReviewRequest(
					"INVALID_TYPE", restaurantId, serviceId, 4, "맛있는 식당이었습니다.", true);

			// when
			ResultActions actions = mockMvc.perform(post("/api/v1/reviews")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(USER_ID_HEADER, "1")
					.header(USER_ROLE_HEADER, "CUSTOMER"));

			// then
			actions.andExpect(status().isBadRequest());
		}

		@Test
		void rating이_범위를_벗어나면_400_상태_코드를_반환한다() throws Exception {
			// given
			CreateReviewRequest request = new CreateReviewRequest(
					"RESERVATION", restaurantId, serviceId, 6, "맛있는 식당이었습니다.", true);

			// when
			ResultActions actions = mockMvc.perform(post("/api/v1/reviews")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(USER_ID_HEADER, "1")
					.header(USER_ROLE_HEADER, "CUSTOMER"));

			// then
			actions.andExpect(status().isBadRequest());
		}

		@Test
		void 필수_값이_null이면_400_상태_코드를_반환한다() throws Exception {
			// given
			CreateReviewRequest request = new CreateReviewRequest(
					"RESERVATION", null, serviceId, 4, "맛있는 식당이었습니다.", true);

			// when
			ResultActions actions = mockMvc.perform(post("/api/v1/reviews")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(USER_ID_HEADER, "1")
					.header(USER_ROLE_HEADER, "CUSTOMER"));

			// then
			actions.andExpect(status().isBadRequest());
		}

		@Test
		void content가_빈_문자열이면_400_상태_코드를_반환한다() throws Exception {
			// given
			CreateReviewRequest request = new CreateReviewRequest(
					"RESERVATION", restaurantId, serviceId, 4, "", true);

			// when
			ResultActions actions = mockMvc.perform(post("/api/v1/reviews")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(USER_ID_HEADER, "1")
					.header(USER_ROLE_HEADER, "CUSTOMER"));

			// then
			actions.andExpect(status().isBadRequest());
		}
	}

	@Nested
	class 리뷰_단건_조회시 {

		private String restaurantId;
		private String serviceId;
		private String reviewId;
		private CurrentUserInfoDto userInfo;
		private GetReviewInfo reviewInfo;

		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID().toString();
			serviceId = UUID.randomUUID().toString();
			reviewId = UUID.randomUUID().toString();
			userInfo = CurrentUserInfoDto.of(123L, UserRole.CUSTOMER);
			reviewInfo = GetReviewInfo.builder()
					.reviewUuid(reviewId)
					.customerId(userInfo.userId())
					.restaurantId(restaurantId)
					.serviceId(serviceId)
					.serviceType("RESERVATION")
					.rating(4)
					.content("맛있는 식당이었습니다.")
					.isVisible(true)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();
		}

		@Test
		void 유효한_요청으로_리뷰를_조회하면_200_상태_코드와_리뷰_정보를_반환한다() throws Exception {
			// given
			when(reviewService.getReview(reviewId, userInfo)).thenReturn(reviewInfo);

			// when
			ResultActions actions = mockMvc.perform(
					get("/api/v1/reviews/{reviewId}", reviewId)
							.header(USER_ID_HEADER, 123L)
							.header(USER_ROLE_HEADER, "CUSTOMER")
			);

			// then
			actions
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.reviewUuid").value(reviewId))
					.andExpect(jsonPath("$.customerId").value(userInfo.userId()))
					.andExpect(jsonPath("$.restaurantId").value(restaurantId))
					.andExpect(jsonPath("$.serviceId").value(serviceId))
					.andExpect(jsonPath("$.serviceType").value("RESERVATION"))
					.andExpect(jsonPath("$.rating").value(4))
					.andExpect(jsonPath("$.content").value("맛있는 식당이었습니다."))
					.andExpect(jsonPath("$.isVisible").value(true));
			verify(reviewService).getReview(reviewId, userInfo);
		}

		@Test
		void 존재하지_않는_리뷰를_조회하면_404_상태코드와_메시지를_반환한다() throws Exception {
			// givem
			when(reviewService.getReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(REVIEW_NOT_FOUND));

			// when
			ResultActions actions = mockMvc.perform(
					get("/api/v1/reviews/{reviewId}", reviewId)
							.header(USER_ID_HEADER, 123L)
							.header(USER_ROLE_HEADER, "CUSTOMER")
			);

			actions.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("해당 리뷰를 찾을 수 없습니다."));
		}

		@Test
		void 권한이_없는_비공개_리뷰에_접근하면_403_상태코드와_메시지를_반환한다() throws Exception {
			// givem
			when(reviewService.getReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(REVIEW_IS_INVISIBLE));

			// when
			ResultActions actions = mockMvc.perform(
					get("/api/v1/reviews/{reviewId}", reviewId)
							.header(USER_ID_HEADER, 123L)
							.header(USER_ROLE_HEADER, "CUSTOMER")
			);

			actions.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.message").value("비공개 처리된 리뷰입니다"));
		}
	}
}