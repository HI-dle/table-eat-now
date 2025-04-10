package table.eat.now.review.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;
import static table.eat.now.review.application.exception.ReviewErrorCode.MODIFY_PERMISSION_DENIED;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_IS_INVISIBLE;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_NOT_FOUND;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
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
import table.eat.now.review.application.service.dto.request.SearchReviewQuery;
import table.eat.now.review.application.service.dto.request.UpdateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;
import table.eat.now.review.application.service.dto.response.PaginatedInfo;
import table.eat.now.review.application.service.dto.response.SearchReviewInfo;
import table.eat.now.review.presentation.dto.request.CreateReviewRequest;
import table.eat.now.review.presentation.dto.request.UpdateReviewRequest;

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

		private UUID restaurantId;
		private UUID serviceId;
		private UUID reviewId;
		private CurrentUserInfoDto userInfo;
		private CreateReviewInfo reviewInfo;


		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID();
			serviceId = UUID.randomUUID();
			reviewId = UUID.randomUUID();
			userInfo = new CurrentUserInfoDto(123L, UserRole.CUSTOMER);

			reviewInfo = CreateReviewInfo.builder()
					.reviewUuid(reviewId.toString())
					.customerId(userInfo.userId())
					.restaurantId(restaurantId.toString())
					.serviceId(serviceId.toString())
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

		private UUID restaurantId;
		private UUID serviceId;
		private UUID reviewId;
		private CurrentUserInfoDto userInfo;
		private GetReviewInfo reviewInfo;

		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID();
			serviceId = UUID.randomUUID();
			reviewId = UUID.randomUUID();
			userInfo = CurrentUserInfoDto.of(123L, UserRole.CUSTOMER);
			reviewInfo = GetReviewInfo.builder()
					.reviewUuid(reviewId.toString())
					.customerId(userInfo.userId())
					.restaurantId(restaurantId.toString())
					.serviceId(serviceId.toString())
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
			when(reviewService.getReview(reviewId.toString(), userInfo)).thenReturn(reviewInfo);

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
			verify(reviewService).getReview(reviewId.toString(), userInfo);
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
			// given
			when(reviewService.getReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(REVIEW_IS_INVISIBLE));

			// when
			ResultActions actions = mockMvc.perform(
					get("/api/v1/reviews/{reviewId}", reviewId)
							.header(USER_ID_HEADER, 123L)
							.header(USER_ROLE_HEADER, "CUSTOMER")
			);

			//then
			actions.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.message").value("비공개 처리된 리뷰입니다."));
		}
	}

	@Nested
	class 리뷰_숨김_요청시 {

		private UUID restaurantId;
		private UUID serviceId;
		private UUID reviewId;
		private CurrentUserInfoDto userInfo;
		private CurrentUserInfoDto otherUserInfo;
		private GetReviewInfo reviewInfo;

		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID();
			serviceId = UUID.randomUUID();
			reviewId = UUID.randomUUID();
			userInfo = CurrentUserInfoDto.of(123L, UserRole.CUSTOMER);
			otherUserInfo = CurrentUserInfoDto.of(456L, UserRole.CUSTOMER);
			reviewInfo = GetReviewInfo.builder()
					.reviewUuid(reviewId.toString())
					.customerId(userInfo.userId())
					.restaurantId(restaurantId.toString())
					.serviceId(serviceId.toString())
					.serviceType("RESERVATION")
					.rating(4)
					.content("맛있는 식당이었습니다.")
					.isVisible(false)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();
		}

		@Test
		void 유효한_요청이면_200_상태_코드와_숨겨진_리뷰_정보를_반환한다() throws Exception {
			// given
			when(reviewService.hideReview(reviewId.toString(), userInfo)).thenReturn(reviewInfo);

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}/hide", reviewId)
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isOk())
					.andExpect(jsonPath("$.reviewUuid").value(reviewId))
					.andExpect(jsonPath("$.customerId").value(userInfo.userId()))
					.andExpect(jsonPath("$.restaurantId").value(restaurantId))
					.andExpect(jsonPath("$.serviceId").value(serviceId))
					.andExpect(jsonPath("$.serviceType").value("RESERVATION"))
					.andExpect(jsonPath("$.rating").value(4))
					.andExpect(jsonPath("$.content").value("맛있는 식당이었습니다."))
					.andExpect(jsonPath("$.isVisible").value(false)); // 숨김 처리 확인

			verify(reviewService).hideReview(reviewId.toString(), userInfo);
		}

		@Test
		void 존재하지_않는_리뷰를_숨기려고_하면_404_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.hideReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(REVIEW_NOT_FOUND));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}/hide", reviewId)
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("해당 리뷰를 찾을 수 없습니다."));
		}

		@Test
		void 권한이_없는_리뷰를_숨기려고_하면_403_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.hideReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(MODIFY_PERMISSION_DENIED));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}/hide", reviewId)
							.header(USER_ID_HEADER, otherUserInfo.userId())
							.header(USER_ROLE_HEADER, otherUserInfo.role())
			);

			// then
			actions.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.message").value("수정 요청에 대한 권한이 없습니다."));
		}
	}

	@Nested
	class 리뷰_공개_시 {

		private UUID restaurantId;
		private UUID serviceId;
		private UUID reviewId;
		private CurrentUserInfoDto userInfo;
		private CurrentUserInfoDto otherUserInfo;
		private GetReviewInfo reviewInfo;

		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID();
			serviceId = UUID.randomUUID();
			reviewId = UUID.randomUUID();
			userInfo = CurrentUserInfoDto.of(123L, UserRole.CUSTOMER);
			otherUserInfo = CurrentUserInfoDto.of(456L, UserRole.CUSTOMER);
			reviewInfo = GetReviewInfo.builder()
					.reviewUuid(reviewId.toString())
					.customerId(userInfo.userId())
					.restaurantId(restaurantId.toString())
					.serviceId(serviceId.toString())
					.serviceType("RESERVATION")
					.rating(4)
					.content("맛있는 식당이었습니다.")
					.isVisible(true)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();
		}

		@Test
		void 유효한_요청으로_리뷰를_공개하면_200_상태_코드와_공개된_리뷰_정보를_반환한다() throws Exception {
			// given
			when(reviewService.showReview(reviewId.toString(), userInfo)).thenReturn(reviewInfo);

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}/show", reviewId)
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
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

			verify(reviewService).showReview(reviewId.toString(), userInfo);
		}

		@Test
		void 존재하지_않는_리뷰를_공개하려고_하면_404_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.showReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(REVIEW_NOT_FOUND));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}/show", reviewId)
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("해당 리뷰를 찾을 수 없습니다."));
		}

		@Test
		void 권한이_없는_리뷰를_공개하려고_하면_403_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.showReview(anyString(), any(CurrentUserInfoDto.class)))
					.thenThrow(CustomException.from(MODIFY_PERMISSION_DENIED));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}/show", reviewId)
							.header(USER_ID_HEADER, otherUserInfo.userId())
							.header(USER_ROLE_HEADER, otherUserInfo.role())
			);

			// then
			actions.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.message").value("수정 요청에 대한 권한이 없습니다."));
		}
	}

	@Nested
	class 리뷰_내용_수정_요청시 {

		private UUID restaurantId;
		private UUID serviceId;
		private UUID reviewId;
		private CurrentUserInfoDto userInfo;
		private CurrentUserInfoDto otherUserInfo;
		private GetReviewInfo reviewInfo;
		private UpdateReviewRequest request;

		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID();
			serviceId = UUID.randomUUID();
			reviewId = UUID.randomUUID();
			userInfo = CurrentUserInfoDto.of(123L, UserRole.CUSTOMER);
			otherUserInfo = CurrentUserInfoDto.of(456L, UserRole.CUSTOMER);
			reviewInfo = GetReviewInfo.builder()
					.reviewUuid(reviewId.toString())
					.customerId(userInfo.userId())
					.restaurantId(restaurantId.toString())
					.serviceId(serviceId.toString())
					.serviceType("RESERVATION")
					.rating(3)
					.content("리뷰 수정합니다요")
					.isVisible(true)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();

			request = new UpdateReviewRequest("리뷰 수정합니다요", 3);
		}

		@Test
		void 유효한_요청으로_리뷰를_수정하면_200_상태_코드와_수정된_리뷰_정보를_반환한다() throws Exception {
			// given
			when(reviewService.updateReview(reviewId.toString(), request.toCommand(userInfo)))
					.thenReturn(reviewInfo);

			// when
			ResultActions actions = mockMvc.perform(patch("/api/v1/reviews/{reviewId}", reviewId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.header(USER_ID_HEADER, userInfo.userId())
					.header(USER_ROLE_HEADER, userInfo.role()));

			// then
			actions.andExpect(status().isOk())
					.andExpect(jsonPath("$.reviewUuid").value(reviewId))
					.andExpect(jsonPath("$.customerId").value(userInfo.userId()))
					.andExpect(jsonPath("$.restaurantId").value(restaurantId))
					.andExpect(jsonPath("$.serviceId").value(serviceId))
					.andExpect(jsonPath("$.serviceType").value("RESERVATION"))
					.andExpect(jsonPath("$.rating").value(3))
					.andExpect(jsonPath("$.content").value("리뷰 수정합니다요"))
					.andExpect(jsonPath("$.isVisible").value(true))
			;

			verify(reviewService).updateReview(reviewId.toString(), request.toCommand(userInfo));
		}

		@Test
		void 존재하지_않는_리뷰를_업데이트하려고_하면_404_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.updateReview(reviewId.toString(), request.toCommand(userInfo)))
					.thenThrow(CustomException.from(REVIEW_NOT_FOUND));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}", reviewId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request))
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.message").value("해당 리뷰를 찾을 수 없습니다."));
		}

		@Test
		void 권한이_없는_리뷰를_업데이트하려고_하면_403_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.updateReview(anyString(), any(UpdateReviewCommand.class)))
					.thenThrow(CustomException.from(MODIFY_PERMISSION_DENIED));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}", reviewId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request))
							.header(USER_ID_HEADER, otherUserInfo.userId())
							.header(USER_ROLE_HEADER, otherUserInfo.role())
			);

			// then
			actions.andExpect(status().isForbidden())
					.andExpect(jsonPath("$.message").value("수정 요청에 대한 권한이 없습니다."));
		}

		@Test
		void 작성자가_아닌_일반유저가_업데이트하려고_하면_400_상태코드와_메시지를_반환한다() throws Exception {
			// given
			when(reviewService.updateReview(anyString(), any(UpdateReviewCommand.class)))
					.thenThrow(new IllegalArgumentException("이 작업에 대한 권한은 작성자에게만 있습니다."));

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}", reviewId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request))
							.header(USER_ID_HEADER, otherUserInfo.userId())
							.header(USER_ROLE_HEADER, otherUserInfo.role())
			);

			// then
			actions.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("이 작업에 대한 권한은 작성자에게만 있습니다."));
		}

		@Test
		void 리뷰_내용이_비어_있으면_400_상태코드를_반환한다() throws Exception {
			// given
			UpdateReviewRequest invalidRequest = new UpdateReviewRequest("", 5);

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}", reviewId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest))
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isBadRequest());
		}

		@Test
		void 평점이_범위를_벗어나면_400_상태코드를_반환한다() throws Exception {
			// given
			UpdateReviewRequest invalidRequest = new UpdateReviewRequest("수정된 리뷰 내용입니다.", 6);

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}", reviewId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest))
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isBadRequest());
		}

		@Test
		void 필수_값이_null이면_400_상태_코드를_반환한다() throws Exception {
			// given
			UpdateReviewRequest invalidRequest =
					new UpdateReviewRequest("수정된 리뷰 내용입니다.", null);

			// when
			ResultActions actions = mockMvc.perform(
					patch("/api/v1/reviews/{reviewId}", reviewId)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(invalidRequest))
							.header(USER_ID_HEADER, userInfo.userId())
							.header(USER_ROLE_HEADER, userInfo.role())
			);

			// then
			actions.andExpect(status().isBadRequest());
		}
	}

	@Nested
	class 리뷰_검색_요청시 {

		private SearchReviewInfo createSearchReviewInfo(
				String reviewUuid,
				Long customerId,
				String restaurantId,
				String serviceId,
				String serviceType,
				int rating,
				String content) {

			return SearchReviewInfo.builder()
					.reviewUuid(reviewUuid)
					.customerId(customerId)
					.restaurantId(restaurantId)
					.serviceId(serviceId)
					.serviceType(serviceType)
					.rating(rating)
					.content(content)
					.isVisible(true)
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();
		}

		private UUID restaurantId;
		private Long userId;
		private CurrentUserInfoDto userInfo;

		@BeforeEach
		void setUp() {
			restaurantId = UUID.randomUUID();
			userId = 1L;
			userInfo = new CurrentUserInfoDto(userId, UserRole.CUSTOMER);
		}

		@Test
		void 유효한_요청으로_리뷰목록을_조회하면_200_상태코드와_페이징된_리뷰목록을_반환한다() throws Exception {
			// given
			SearchReviewInfo myReview = createSearchReviewInfo(
					"review-uuid-1",
					userId,
					restaurantId.toString(),
					"service-1",
					"RESERVATION",
					4,
					"좋은 식당이었습니다."
			);

			SearchReviewInfo otherReview = createSearchReviewInfo(
					"review-uuid-2",
					2L,
					restaurantId.toString(),
					"service-2",
					"WAITING",
					5,
					"최고의 레스토랑입니다!"
			);

			PaginatedInfo<SearchReviewInfo> paginatedInfo = new PaginatedInfo<>(
					List.of(myReview, otherReview), 0, 10, 2L, 1);

			when(
					reviewService.getReviews(any(SearchReviewQuery.class),
							any(CurrentUserInfoDto.class)))
					.thenReturn(paginatedInfo);

			// when
			ResultActions actions = mockMvc.perform(get("/api/v1/reviews")
					.header(USER_ID_HEADER, userInfo.userId())
					.header(USER_ROLE_HEADER, userInfo.role())
					.param("page", "0")
					.param("size", "10"));

			// then
			actions
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content").isArray())
					.andExpect(jsonPath("$.content.length()").value(2))
					.andExpect(jsonPath("$.content[0].reviewUuid").value("review-uuid-1"))
					.andExpect(jsonPath("$.content[0].customerId").value(userId))
					.andExpect(jsonPath("$.content[1].reviewUuid").value("review-uuid-2"))
					.andExpect(jsonPath("$.page").value(0))
					.andExpect(jsonPath("$.size").value(10))
					.andExpect(jsonPath("$.totalElements").value(2))
					.andExpect(jsonPath("$.totalPages").value(1));
		}

		@Test
		void 필터링_파라미터를_포함한_요청시_200_상태코드와_필터링된_리뷰목록을_반환한다() throws Exception {
			// given
			SearchReviewInfo myReview = createSearchReviewInfo(
					"review-uuid-1",
					userId,
					restaurantId.toString(),
					"service-1",
					"RESERVATION",
					4,
					"좋은 식당이었습니다."
			);

			PaginatedInfo<SearchReviewInfo> paginatedInfo = new PaginatedInfo<>(
					List.of(myReview), 0, 10, 1L, 1);

			when(
					reviewService.getReviews(any(SearchReviewQuery.class),
							any(CurrentUserInfoDto.class)))
					.thenReturn(paginatedInfo);

			// when
			ResultActions actions = mockMvc.perform(get("/api/v1/reviews")
					.header(USER_ID_HEADER, userInfo.userId())
					.header(USER_ROLE_HEADER, userInfo.role())
					.param("serviceType", "RESERVATION")
					.param("minRating", "4")
					.param("maxRating", "5")
					.param("userId", userId.toString())
					.param("isVisible", "true")
					.param("page", "0")
					.param("size", "10"));

			// then
			actions
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content").isArray())
					.andExpect(jsonPath("$.content.length()").value(1))
					.andExpect(jsonPath("$.content[0].reviewUuid").value("review-uuid-1"))
					.andExpect(jsonPath("$.content[0].serviceType").value("RESERVATION"))
					.andExpect(jsonPath("$.content[0].rating").value(4))
					.andExpect(jsonPath("$.content[0].isVisible").value(true));
		}

		@Test
		void 잘못된_서비스_타입으로_요청시_400_상태코드를_반환한다() throws Exception {
			// when
			ResultActions actions = mockMvc.perform(get("/api/v1/reviews")
					.header(USER_ID_HEADER, userInfo.userId())
					.header(USER_ROLE_HEADER, userInfo.role())
					.param("serviceType", "INVALID_TYPE")
					.param("page", "0")
					.param("size", "10"));

			// then
			actions.andExpect(status().isBadRequest());
		}

		@Test
		void 잘못된_정렬_필드로_요청시_400_상태코드를_반환한다() throws Exception {
			// when
			ResultActions actions = mockMvc.perform(get("/api/v1/reviews")
					.header(USER_ID_HEADER, userInfo.userId())
					.header(USER_ROLE_HEADER, userInfo.role())
					.param("orderBy", "invalidField"));

			// then
			actions.andExpect(status().isBadRequest());
		}
	}
}
