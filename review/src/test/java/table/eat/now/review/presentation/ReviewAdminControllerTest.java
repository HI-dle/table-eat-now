package table.eat.now.review.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;
import static table.eat.now.common.resolver.dto.UserRole.CUSTOMER;
import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.common.resolver.dto.UserRole.OWNER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.type.ApiErrorCode;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.service.dto.request.SearchAdminReviewQuery;
import table.eat.now.review.application.service.dto.response.PaginatedInfo;
import table.eat.now.review.application.service.dto.response.SearchAdminReviewInfo;
import table.eat.now.review.global.support.ControllerTestSupport;

class ReviewAdminControllerTest extends ControllerTestSupport {

  @Nested
  class 관리자_리뷰_검색_요청시 {

    private SearchAdminReviewInfo createSearchAdminReviewInfo(
        String reviewUuid,
        Long customerId,
        String restaurantId,
        String serviceId,
        String serviceType,
        int rating,
        String content,
        boolean isVisible,
        Long hiddenBy,
        String hiddenByRole) {

      return SearchAdminReviewInfo.builder()
          .reviewUuid(reviewUuid)
          .customerId(customerId)
          .restaurantId(restaurantId)
          .serviceId(serviceId)
          .serviceType(serviceType)
          .rating(rating)
          .content(content)
          .isVisible(isVisible)
          .hiddenBy(hiddenBy)
          .hiddenByRole(hiddenByRole)
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build();
    }

    private UUID restaurantId;
    private Long userId;
    private CurrentUserInfoDto masterUserInfo;
    private CurrentUserInfoDto ownerUserInfo;

    @BeforeEach
    void setUp() {
      restaurantId = UUID.randomUUID();
      userId = 1L;
      masterUserInfo = new CurrentUserInfoDto(userId, MASTER);
      ownerUserInfo = new CurrentUserInfoDto(userId, OWNER);
    }

    @Test
    void 마스터_권한으로_리뷰목록_조회시_200_상태코드와_페이징된_리뷰목록을_반환한다() throws Exception {
      // given
      SearchAdminReviewInfo userReview = createSearchAdminReviewInfo(
          "review-uuid-1",
          2L,
          restaurantId.toString(),
          "service-1",
          "RESERVATION",
          4,
          "좋은 식당이었습니다.",
          true,
          null,
          null
      );

      SearchAdminReviewInfo hiddenReview = createSearchAdminReviewInfo(
          "review-uuid-2",
          3L,
          restaurantId.toString(),
          "service-2",
          "WAITING",
          2,
          "실망스러웠습니다.",
          false,
          1L,
          "MASTER"
      );

      PaginatedInfo<SearchAdminReviewInfo> paginatedInfo = new PaginatedInfo<>(
          List.of(userReview, hiddenReview), 0, 10, 2L, 1);

      when(
          reviewService.searchAdminReviews(any(SearchAdminReviewQuery.class),
              any(CurrentUserInfoDto.class)))
          .thenReturn(paginatedInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/reviews")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("page", "0")
          .param("size", "10"));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(2))
          .andExpect(jsonPath("$.content[0].reviewUuid").value("review-uuid-1"))
          .andExpect(jsonPath("$.content[0].isVisible").value(true))
          .andExpect(jsonPath("$.content[1].reviewUuid").value("review-uuid-2"))
          .andExpect(jsonPath("$.content[1].isVisible").value(false))
          .andExpect(jsonPath("$.content[1].hiddenBy").value(1))
          .andExpect(jsonPath("$.content[1].hiddenByRole").value("MASTER"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void 식당_주인_권한으로_리뷰목록_조회시_200_상태코드와_페이징된_리뷰목록을_반환한다() throws Exception {
      // given
      SearchAdminReviewInfo userReview = createSearchAdminReviewInfo(
          "review-uuid-1",
          2L,
          restaurantId.toString(),
          "service-1",
          "RESERVATION",
          4,
          "좋은 식당이었습니다.",
          true,
          null,
          null
      );

      PaginatedInfo<SearchAdminReviewInfo> paginatedInfo = new PaginatedInfo<>(
          List.of(userReview), 0, 10, 1L, 1);

      when(
          reviewService.searchAdminReviews(any(SearchAdminReviewQuery.class),
              any(CurrentUserInfoDto.class)))
          .thenReturn(paginatedInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/reviews")
          .header(USER_ID_HEADER, ownerUserInfo.userId())
          .header(USER_ROLE_HEADER, ownerUserInfo.role())
          .param("page", "0")
          .param("size", "10"));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.content[0].reviewUuid").value("review-uuid-1"))
          .andExpect(jsonPath("$.content[0].isVisible").value(true))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void 필터링_파라미터를_포함한_요청시_200_상태코드와_필터링된_리뷰목록을_반환한다() throws Exception {
      // given
      SearchAdminReviewInfo filteredReview = createSearchAdminReviewInfo(
          "review-uuid-1",
          2L,
          restaurantId.toString(),
          "service-1",
          "RESERVATION",
          4,
          "좋은 식당이었습니다.",
          true,
          null,
          null
      );

      PaginatedInfo<SearchAdminReviewInfo> paginatedInfo = new PaginatedInfo<>(
          List.of(filteredReview), 0, 10, 1L, 1);

      when(
          reviewService.searchAdminReviews(any(SearchAdminReviewQuery.class),
              any(CurrentUserInfoDto.class)))
          .thenReturn(paginatedInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/reviews")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("serviceType", "RESERVATION")
          .param("minRating", "4")
          .param("maxRating", "5")
          .param("userId", "2")
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
    void 권한이_없는_사용자가_접근시_403_상태코드를_반환한다() throws Exception {
      // given
      CurrentUserInfoDto customerUserInfo = new CurrentUserInfoDto(userId, CUSTOMER);
      doThrow(CustomException.from(ApiErrorCode.FORBIDDEN))
          .when(reviewService).searchAdminReviews(any(SearchAdminReviewQuery.class), any(CurrentUserInfoDto.class));

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/reviews")
          .header(USER_ID_HEADER, customerUserInfo.userId())
          .header(USER_ROLE_HEADER, customerUserInfo.role())
          .param("page", "0")
          .param("size", "10"));

      // then
      actions.andExpect(status().isForbidden());
    }

    @Test
    void 잘못된_서비스_타입으로_요청시_400_상태코드를_반환한다() throws Exception {
      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/reviews")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("serviceType", "INVALID_TYPE")
          .param("page", "0")
          .param("size", "10"));

      // then
      actions.andExpect(status().isBadRequest());
    }

    @Test
    void 잘못된_정렬_필드로_요청시_400_상태코드를_반환한다() throws Exception {
      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/reviews")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("orderBy", "invalidField"));

      // then
      actions.andExpect(status().isBadRequest());
    }
  }
}