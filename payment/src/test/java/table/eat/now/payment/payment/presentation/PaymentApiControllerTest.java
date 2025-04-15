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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.aop.AuthCheckAspect;
import table.eat.now.common.config.WebConfig;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.exception.type.ApiErrorCode;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;
import table.eat.now.payment.payment.presentation.dto.request.ConfirmPaymentRequest;

@ActiveProfiles("test")
@Import({
    WebConfig.class,
    CustomPageableArgumentResolver.class,
    CurrentUserInfoResolver.class,
    GlobalErrorHandler.class,
    AuthCheckAspect.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@WebMvcTest(PaymentApiController.class)
class PaymentApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PaymentService paymentService;

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
}