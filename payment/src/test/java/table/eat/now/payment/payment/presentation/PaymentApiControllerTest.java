package table.eat.now.payment.payment.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_ACCESS_DENIED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.type.ApiErrorCode;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.PaginatedInfo;
import table.eat.now.payment.payment.application.dto.response.SearchPaymentsInfo;
import table.eat.now.payment.payment.global.support.ControllerTestSupport;
import table.eat.now.payment.payment.presentation.dto.request.ConfirmPaymentRequest;

class PaymentApiControllerTest extends ControllerTestSupport {

  @Nested
  class 체크아웃_정보_조회_시 {

    private UUID idempotencyKey;
    private GetCheckoutDetailInfo checkoutDetailInfo;

    @BeforeEach
    void setUp() {
      idempotencyKey = UUID.randomUUID();
      checkoutDetailInfo = new GetCheckoutDetailInfo(
          idempotencyKey.toString(),
          "customer123",
          BigDecimal.valueOf(10000),
          "맛있는 식당 예약",
          "reservation123"
      );
    }

    @Test
    void 유효한_idempotencyKey로_요청하면_200_상태코드와_체크아웃_정보를_반환한다() throws Exception {
      // given
      when(paymentService.getCheckoutDetail(idempotencyKey.toString()))
          .thenReturn(checkoutDetailInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/checkout-info")
          .param("idempotencyKey", idempotencyKey.toString())
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.idempotencyKey").value(idempotencyKey.toString()))
          .andExpect(jsonPath("$.customerKey").value("customer123"))
          .andExpect(jsonPath("$.originalAmount").value(10000))
          .andExpect(jsonPath("$.reservationId").value("reservation123"))
          .andExpect(jsonPath("$.reservationName").value("맛있는 식당 예약"));
    }

    @Test
    void 유효하지_않은_idempotencyKey_입력시_400상태코드와_메시지를_반환한다() throws Exception {
      // given
      String invalidUUID = "invalid-uuid";

      // when
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/checkout-info")
          .param("idempotencyKey", invalidUUID)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(ApiErrorCode.TYPE_MISMATCH.getMessage()));
    }
  }

  @Nested
  class 결제_확인_요청_시 {

    private UUID reservationUuid;
    private ConfirmPaymentRequest confirmRequest;
    private ConfirmPaymentInfo confirmPaymentInfo;

    @BeforeEach
    void setUp() {
      reservationUuid = UUID.randomUUID();
      confirmRequest = new ConfirmPaymentRequest(
          "payment_key_123456",
          BigDecimal.valueOf(10000)
      );

      confirmPaymentInfo = ConfirmPaymentInfo.builder()
          .paymentUuid(UUID.randomUUID().toString())
          .customerId(123L)
          .reservationId(reservationUuid.toString())
          .reservationName("맛있는 식당 예약")
          .paymentStatus("DONE")
          .originalAmount(BigDecimal.valueOf(10000))
          .discountAmount(BigDecimal.ZERO)
          .totalAmount(BigDecimal.valueOf(10000))
          .createdAt(LocalDateTime.now())
          .approvedAt(LocalDateTime.now())
          .cancelledAt(null)
          .build();
    }

    @Test
    void 유효한_요청으로_결제_확인시_200_상태코드와_결제_정보를_반환한다() throws Exception {
      // given
      when(paymentService.confirmPayment(any(), any())).thenReturn(confirmPaymentInfo);

      // when
      ResultActions actions = mockMvc.perform(patch("/api/v1/payments/confirm")
          .param("reservationUuid", reservationUuid.toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(confirmRequest)));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.paymentUuid").value(confirmPaymentInfo.paymentUuid()))
          .andExpect(jsonPath("$.customerId").value(confirmPaymentInfo.customerId()))
          .andExpect(jsonPath("$.reservationId").value(confirmPaymentInfo.reservationId()))
          .andExpect(jsonPath("$.reservationName").value(confirmPaymentInfo.reservationName()))
          .andExpect(jsonPath("$.paymentStatus").value(confirmPaymentInfo.paymentStatus()))
          .andExpect(
              jsonPath("$.originalAmount").value(confirmPaymentInfo.originalAmount().intValue()))
          .andExpect(jsonPath("$.totalAmount").value(confirmPaymentInfo.totalAmount().intValue()));
    }

    @Test
    void 유효하지_않은_reservationUuid_입력시_400_상태코드와_메시지를_반환한다() throws Exception {
      // given
      String invalidUUID = "invalid-uuid";

      // when
      ResultActions actions = mockMvc.perform(patch("/api/v1/payments/confirm")
          .param("reservationUuid", invalidUUID)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(confirmRequest)));

      // then
      actions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(ApiErrorCode.TYPE_MISMATCH.getMessage()));
    }

    @Test
    void 유효하지_않은_요청_본문_입력시_Bad_Request를_반환한다() throws Exception {
      // given
      ConfirmPaymentRequest invalidRequest =
          new ConfirmPaymentRequest(null, null);
      // when
      ResultActions actions = mockMvc.perform(patch("/api/v1/payments/confirm")
          .param("reservationUuid", reservationUuid.toString())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest)));

      // then
      actions.andExpect(status().isBadRequest());
    }
  }

  @Nested
  class 결제_단일_조회_시 {

    private UUID paymentUuid;
    private GetPaymentInfo paymentInfo;
    private CurrentUserInfoDto userInfo;

    @BeforeEach
    void setUp() {
      paymentUuid = UUID.randomUUID();
      userInfo = new CurrentUserInfoDto(123L, UserRole.CUSTOMER);

      paymentInfo = GetPaymentInfo.builder()
          .paymentUuid(paymentUuid.toString())
          .customerId(123L)
          .reservationId("reservation123")
          .restaurantId("restaurant123")
          .reservationName("맛있는 식당 예약")
          .paymentStatus("APPROVED")
          .originalAmount(BigDecimal.valueOf(15000))
          .discountAmount(BigDecimal.valueOf(2000))
          .totalAmount(BigDecimal.valueOf(13000))
          .createdAt(LocalDateTime.now())
          .approvedAt(LocalDateTime.now().plusMinutes(10))
          .cancelledAt(null)
          .build();
    }

    @Test
    void 유효한_paymentUuid로_요청하면_200_상태코드와_결제_정보를_반환한다() throws Exception {
      // given
      when(paymentService.getPayment(paymentUuid.toString(), userInfo))
          .thenReturn(paymentInfo);

      // when
      ResultActions actions =
          mockMvc.perform(get("/api/v1/payments/{paymentUuid}", paymentUuid)
          .header(USER_ID_HEADER, userInfo.userId())
          .header(USER_ROLE_HEADER, userInfo.role())
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.paymentUuid").value(paymentUuid.toString()))
          .andExpect(jsonPath("$.customerId").value(123L))
          .andExpect(jsonPath("$.reservationId").value("reservation123"))
          .andExpect(jsonPath("$.restaurantId").value("restaurant123"))
          .andExpect(jsonPath("$.reservationName").value("맛있는 식당 예약"))
          .andExpect(jsonPath("$.paymentStatus").value("APPROVED"))
          .andExpect(jsonPath("$.originalAmount").value(15000))
          .andExpect(jsonPath("$.discountAmount").value(2000))
          .andExpect(jsonPath("$.totalAmount").value(13000));
    }

    @Test
    void 유효하지_않은_paymentUuid_입력시_400_상태코드와_메시지를_반환한다() throws Exception {
      // given
      String invalidUUID = "invalid-uuid";

      // when
      ResultActions actions =
          mockMvc.perform(get("/api/v1/payments/{paymentUuid}", invalidUUID)
          .header(USER_ID_HEADER, userInfo.userId())
          .header(USER_ROLE_HEADER, userInfo.role())
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(ApiErrorCode.TYPE_MISMATCH.getMessage()));
    }

    @Test
    void 사용자_인증_정보가_없을때_401_상태코드를_반환한다() throws Exception {
      // when
      ResultActions actions =
          mockMvc.perform(get("/api/v1/payments/{paymentUuid}", paymentUuid)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isUnauthorized());
    }

    @Test
    void 다른_사용자의_결제_정보_요청시_403_상태코드를_반환한다() throws Exception {
      // given
      when(paymentService.getPayment(anyString(), any(CurrentUserInfoDto.class)))
          .thenThrow(CustomException.from(PAYMENT_ACCESS_DENIED));

      // when
      ResultActions actions =
          mockMvc.perform(get("/api/v1/payments/{paymentUuid}", paymentUuid)
          .header(USER_ID_HEADER, 456L)
          .header(USER_ROLE_HEADER, UserRole.CUSTOMER)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message")
              .value(PAYMENT_ACCESS_DENIED.getMessage()));
    }
  }

  @Nested
  class 내_결제_목록_조회_시 {

    private CurrentUserInfoDto userInfo;
    private PaginatedInfo<SearchPaymentsInfo> paginatedInfo;
    private List<SearchPaymentsInfo> paymentInfoList;

    @BeforeEach
    void setUp() {
      userInfo = new CurrentUserInfoDto(123L, UserRole.CUSTOMER);

      // 테스트 결제 정보 생성
      paymentInfoList = List.of(
          createSearchMyPaymentInfo(UUID.randomUUID().toString(), "reservation1", "APPROVED"),
          createSearchMyPaymentInfo(UUID.randomUUID().toString(), "reservation2", "CANCELED")
      );

      // 페이징된 결제 정보 생성
      paginatedInfo = new PaginatedInfo<>(
          paymentInfoList,
          0,
          10,
          2L,
          1
      );
    }

    private SearchPaymentsInfo createSearchMyPaymentInfo(
        String paymentUuid, String reservationId, String paymentStatus) {
      return SearchPaymentsInfo.builder()
          .paymentUuid(paymentUuid)
          .customerId(123L)
          .paymentKey("payment_key_" + UUID.randomUUID().toString().substring(0, 8))
          .reservationId(reservationId)
          .restaurantId("restaurant123")
          .reservationName("맛있는 식당 예약")
          .paymentStatus(paymentStatus)
          .originalAmount(BigDecimal.valueOf(15000))
          .discountAmount(BigDecimal.valueOf(2000))
          .totalAmount(BigDecimal.valueOf(13000))
          .createdAt(LocalDateTime.now())
          .approvedAt("APPROVED".equals(paymentStatus) ? LocalDateTime.now().plusMinutes(10) : null)
          .cancelledAt("CANCELED".equals(paymentStatus) ? LocalDateTime.now().plusMinutes(20) : null)
          .build();
    }

    @Test
    void 유효한_요청으로_내_결제_목록_조회시_200_상태코드와_페이징된_결제_목록을_반환한다() throws Exception {
      // given
      when(paymentService.searchMyPayments(any()))
          .thenReturn(paginatedInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/my")
          .header(USER_ID_HEADER, userInfo.userId())
          .header(USER_ROLE_HEADER, userInfo.role())
          .param("page", "0")
          .param("size", "10")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(2))
          .andExpect(jsonPath("$.content[0].paymentUuid").value(paymentInfoList.get(0).paymentUuid()))
          .andExpect(jsonPath("$.content[0].paymentStatus").value("APPROVED"))
          .andExpect(jsonPath("$.content[1].paymentUuid").value(paymentInfoList.get(1).paymentUuid()))
          .andExpect(jsonPath("$.content[1].paymentStatus").value("CANCELED"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void 필터링_파라미터를_포함한_요청시_200_상태코드와_필터링된_결제_목록을_반환한다() throws Exception {
      // given
      UUID restaurantUuid = UUID.randomUUID();
      String paymentStatus = "APPROVED";
      LocalDate startDate = LocalDate.now().minusDays(30);
      LocalDate endDate = LocalDate.now();

      List<SearchPaymentsInfo> filteredList = List.of(
          createSearchMyPaymentInfo(UUID.randomUUID().toString(), "reservation1", "APPROVED")
      );

      PaginatedInfo<SearchPaymentsInfo> filteredInfo = new PaginatedInfo<>(
          filteredList,
          0,
          10,
          1L,
          1
      );

      when(paymentService.searchMyPayments(any()))
          .thenReturn(filteredInfo);

      // when
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/my")
          .header(USER_ID_HEADER, userInfo.userId())
          .header(USER_ROLE_HEADER, userInfo.role())
          .param("restaurantUuid", restaurantUuid.toString())
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
    }

    @Test
    void 잘못된_날짜_형식으로_요청시_400_상태코드와_메시지를_반환한다() throws Exception {
      // given
      String invalidDate = "invalid-date";

      // when
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/my")
          .header(USER_ID_HEADER, userInfo.userId())
          .header(USER_ROLE_HEADER, userInfo.role())
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
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/my")
          .header(USER_ID_HEADER, userInfo.userId())
          .header(USER_ROLE_HEADER, userInfo.role())
          .param("orderBy", "invalidField")
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isBadRequest());
    }
  }
}