package table.eat.now.payment.payment.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import table.eat.now.common.exception.GlobalErrorHandler;
import table.eat.now.common.resolver.CurrentUserInfoResolver;
import table.eat.now.common.resolver.CustomPageableArgumentResolver;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
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
      given(paymentService.getCheckoutDetail(idempotencyKey.toString())).willReturn(checkoutDetailInfo);

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
    void 유효하지_않은_idempotencyKey_입력시_InternalServerError를_반환한다() throws Exception {
      // given
      String invalidUUID = "invalid-uuid";

      // when
      ResultActions actions = mockMvc.perform(get("/api/v1/payments/checkout-info")
          .param("idempotencyKey", invalidUUID)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isInternalServerError());
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
      given(paymentService.confirmPayment(any())).willReturn(confirmPaymentInfo);

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
          .andExpect(jsonPath("$.originalAmount").value(confirmPaymentInfo.originalAmount().intValue()))
          .andExpect(jsonPath("$.totalAmount").value(confirmPaymentInfo.totalAmount().intValue()));
    }

    @Test
    void 유효하지_않은_reservationUuid_입력시_InternalServerError를_반환한다() throws Exception {
      // given
      String invalidUUID = "invalid-uuid";

      // when
      ResultActions actions = mockMvc.perform(patch("/api/v1/payments/confirm")
          .param("reservationUuid", invalidUUID)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(confirmRequest)));

      // then
      actions.andExpect(status().isInternalServerError());
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
}