package table.eat.now.review.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.application.service.ReviewService;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.presentation.dto.request.CreateReviewRequest;

@ActiveProfiles("test")
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ReviewService reviewService;

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
				.andExpect(jsonPath("$.reviewUuid").value(reviewId.toString()))
				.andExpect(jsonPath("$.customerId").value(userInfo.userId()))
				.andExpect(jsonPath("$.restaurantId").value(restaurantId.toString()))
				.andExpect(jsonPath("$.serviceId").value(serviceId.toString()))
				.andExpect(jsonPath("$.serviceType").value("RESERVATION"))
				.andExpect(jsonPath("$.rating").value(4))
				.andExpect(jsonPath("$.content").value("맛있는 식당이었습니다."))
				.andExpect(jsonPath("$.isVisible").value(true));

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
				.requestAttr("currentUser", userInfo));

		// then
		actions.andExpect(status().isBadRequest());
	}
}