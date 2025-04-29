package table.eat.now.payment.payment.presentation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.exception.type.ApiErrorCode;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.payment.payment.application.dto.request.SearchMasterPaymentsQuery;
import table.eat.now.payment.payment.application.dto.response.PaginatedInfo;
import table.eat.now.payment.payment.application.dto.response.SearchPaymentsInfo;
import table.eat.now.payment.payment.global.support.ControllerTestSupport;

class PaymentAdminControllerTest extends ControllerTestSupport {

  @Nested
  class 관리자_결제_목록_조회_시 {

    private CurrentUserInfoDto masterUserInfo;
    private PaginatedInfo<SearchPaymentsInfo> paginatedInfo;
    private List<SearchPaymentsInfo> paymentInfoList;

    @BeforeEach
    void setUp() {
      masterUserInfo = new CurrentUserInfoDto(999L, UserRole.MASTER);

      paymentInfoList = List.of(
          createSearchPaymentInfo(UUID.randomUUID().toString(), 123L, "reservation1", "APPROVED"),
          createSearchPaymentInfo(UUID.randomUUID().toString(), 456L, "reservation2", "CANCELED"),
          createSearchPaymentInfo(UUID.randomUUID().toString(), 789L, "reservation3", "PENDING")
      );

      paginatedInfo = new PaginatedInfo<>(
          paymentInfoList,
          0,
          10,
          3L,
          1
      );
    }

    private SearchPaymentsInfo createSearchPaymentInfo(
        String paymentUuid, Long customerId, String reservationId, String paymentStatus) {
      return SearchPaymentsInfo.builder()
          .paymentUuid(paymentUuid)
          .customerId(customerId)
          .paymentKey("payment_key_" + UUID.randomUUID().toString().substring(0, 8))
          .reservationId(reservationId)
          .restaurantId("restaurant123")
          .reservationName("고객 " + customerId + "의 예약")
          .paymentStatus(paymentStatus)
          .originalAmount(BigDecimal.valueOf(15000))
          .discountAmount(BigDecimal.valueOf(2000))
          .totalAmount(BigDecimal.valueOf(13000))
          .createdAt(LocalDateTime.now())
          .approvedAt("APPROVED".equals(paymentStatus) ? LocalDateTime.now().plusMinutes(10) : null)
          .cancelledAt(
              "CANCELED".equals(paymentStatus) ? LocalDateTime.now().plusMinutes(20) : null)
          .build();
    }

    @Test
    void 유효한_요청으로_모든_결제_목록_조회시_200_상태코드와_페이징된_결제_목록을_반환한다() throws Exception {
      // given
      when(paymentService.searchMasterPayments(any()))
          .thenReturn(paginatedInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/payments")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("page", "0")
          .param("size", "10")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(3))
          .andExpect(
              jsonPath("$.content[0].paymentUuid").value(paymentInfoList.get(0).paymentUuid()))
          .andExpect(jsonPath("$.content[0].customerId").value(123))
          .andExpect(jsonPath("$.content[0].paymentStatus").value("APPROVED"))
          .andExpect(
              jsonPath("$.content[1].paymentUuid").value(paymentInfoList.get(1).paymentUuid()))
          .andExpect(jsonPath("$.content[1].customerId").value(456))
          .andExpect(jsonPath("$.content[1].paymentStatus").value("CANCELED"))
          .andExpect(
              jsonPath("$.content[2].paymentUuid").value(paymentInfoList.get(2).paymentUuid()))
          .andExpect(jsonPath("$.content[2].customerId").value(789))
          .andExpect(jsonPath("$.content[2].paymentStatus").value("PENDING"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(3))
          .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void 특정_사용자_필터링_요청시_200_상태코드와_필터링된_결제_목록을_반환한다() throws Exception {
      // given
      Long specificUserId = 123L;

      List<SearchPaymentsInfo> filteredList = List.of(
          createSearchPaymentInfo(UUID.randomUUID().toString(), specificUserId, "reservation1",
              "APPROVED")
      );

      PaginatedInfo<SearchPaymentsInfo> filteredInfo = new PaginatedInfo<>(
          filteredList,
          0,
          10,
          1L,
          1
      );

      when(paymentService.searchMasterPayments(any()))
          .thenReturn(filteredInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/payments")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("userId", specificUserId.toString())
          .param("page", "0")
          .param("size", "10")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.content[0].customerId").value(specificUserId));

      ArgumentCaptor<SearchMasterPaymentsQuery> queryCaptor = ArgumentCaptor.forClass(
          SearchMasterPaymentsQuery.class);
      verify(paymentService).searchMasterPayments(queryCaptor.capture());

      SearchMasterPaymentsQuery capturedQuery = queryCaptor.getValue();
      assertThat(capturedQuery.userId()).isEqualTo(specificUserId);
    }

    @Test
    void 다양한_필터링_파라미터를_포함한_요청시_200_상태코드와_필터링된_결제_목록을_반환한다() throws Exception {
      // given
      UUID restaurantUuid = UUID.randomUUID();
      String paymentStatus = "APPROVED";
      LocalDate startDate = LocalDate.now().minusDays(30);
      LocalDate endDate = LocalDate.now();

      List<SearchPaymentsInfo> filteredList = List.of(
          createSearchPaymentInfo(UUID.randomUUID().toString(), 123L, "reservation1", "APPROVED")
      );

      PaginatedInfo<SearchPaymentsInfo> filteredInfo = new PaginatedInfo<>(
          filteredList,
          0,
          10,
          1L,
          1
      );

      when(paymentService.searchMasterPayments(any()))
          .thenReturn(filteredInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/payments")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("restaurantUuid", restaurantUuid.toString())
          .param("userId", "123")
          .param("paymentStatus", paymentStatus)
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
          .param("orderBy", "createdAt")
          .param("sort", "desc")
          .param("page", "0")
          .param("size", "10")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.content[0].paymentStatus").value("APPROVED"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.totalPages").value(1));

      ArgumentCaptor<SearchMasterPaymentsQuery> queryCaptor = ArgumentCaptor.forClass(
          SearchMasterPaymentsQuery.class);
      verify(paymentService).searchMasterPayments(queryCaptor.capture());

      SearchMasterPaymentsQuery capturedQuery = queryCaptor.getValue();
      assertThat(capturedQuery.userId()).isEqualTo(123L);
      assertThat(capturedQuery.restaurantUuid()).isEqualTo(restaurantUuid.toString());
      assertThat(capturedQuery.paymentStatus()).isEqualTo(paymentStatus);
      assertThat(capturedQuery.startDate()).isEqualTo(startDate);
      assertThat(capturedQuery.endDate()).isEqualTo(endDate);
      assertThat(capturedQuery.orderBy()).isEqualTo("createdAt");
      assertThat(capturedQuery.sort()).isEqualTo("desc");
    }

    @Test
    void 잘못된_날짜_형식으로_요청시_400_상태코드와_메시지를_반환한다() throws Exception {
      // given
      String invalidDate = "invalid-date";

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/payments")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("startDate", invalidDate)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(ApiErrorCode.INVALID_REQUEST.getMessage()));
    }

    @Test
    void 잘못된_정렬_필드로_요청시_400_상태코드를_반환한다() throws Exception {
      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/payments")
          .header(USER_ID_HEADER, masterUserInfo.userId())
          .header(USER_ROLE_HEADER, masterUserInfo.role())
          .param("orderBy", "invalidField")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest());
    }

    @Test
    void 마스터가_아닌_사용자로_요청시_403_상태코드를_반환한다() throws Exception {
      // given
      CurrentUserInfoDto customerUserInfo = new CurrentUserInfoDto(123L, UserRole.CUSTOMER);

      // when
      ResultActions actions = mockMvc.perform(get("/admin/v1/payments")
          .header(USER_ID_HEADER, customerUserInfo.userId())
          .header(USER_ROLE_HEADER, customerUserInfo.role())
          .param("page", "0")
          .param("size", "10")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isForbidden());
    }
  }

}