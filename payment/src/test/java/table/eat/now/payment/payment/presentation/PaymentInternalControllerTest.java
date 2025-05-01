package table.eat.now.payment.payment.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_PENDING;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import table.eat.now.common.exception.CustomException;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.global.support.ControllerTestSupport;
import table.eat.now.payment.payment.presentation.dto.request.CreatePaymentRequest;

class PaymentInternalControllerTest extends ControllerTestSupport {

  @Nested
  class 결제_생성_요청시 {

    private UUID reservationUuid;
    private UUID restaurantUuid;
    private Long customerId;
    private String reservationName;
    private BigDecimal originalAmount;
    private CreatePaymentRequest request;
    private CreatePaymentInfo paymentInfo;

    @BeforeEach
    void setUp() {
      reservationUuid = UUID.randomUUID();
      restaurantUuid = UUID.randomUUID();
      customerId = 123L;
      reservationName = "고객님의 예약";
      originalAmount = BigDecimal.valueOf(50000);

      request = new CreatePaymentRequest(
          reservationUuid.toString(),
          restaurantUuid.toString(),
          customerId,
          reservationName,
          originalAmount
      );

      paymentInfo = CreatePaymentInfo.builder()
          .paymentUuid(UUID.randomUUID().toString())
          .idempotencyKey(UUID.randomUUID().toString())
          .paymentStatus("CREATED")
          .originalAmount(originalAmount)
          .createdAt(LocalDateTime.now())
          .build();
    }

    @Test
    void 유효한_요청으로_결제를_생성하면_201_상태_코드와_생성된_결제_정보를_반환한다() throws Exception {
      // given
      when(paymentService.createPayment(any(CreatePaymentCommand.class))).thenReturn(paymentInfo);

      // when
      ResultActions actions = mockMvc.perform(post("/internal/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.paymentUuid").value(paymentInfo.paymentUuid()))
          .andExpect(jsonPath("$.idempotencyKey").value(paymentInfo.idempotencyKey()))
          .andExpect(jsonPath("$.paymentStatus").value(paymentInfo.paymentStatus()))
          .andExpect(jsonPath("$.originalAmount").value(originalAmount.intValue()))
          .andExpect(jsonPath("$.createdAt").exists());

      verify(paymentService).createPayment(any(CreatePaymentCommand.class));
    }

    @Test
    void 결제_금액_불일치_예외가_발생하면_400_상태_코드를_반환한다() throws Exception {
      // given
      when(paymentService.createPayment(any(CreatePaymentCommand.class)))
          .thenThrow(CustomException.from(PAYMENT_AMOUNT_MISMATCH));

      // when
      ResultActions actions = mockMvc.perform(post("/internal/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value(PAYMENT_AMOUNT_MISMATCH.getMessage()));
    }

    @Test
    void 예약_상태_예외가_발생하면_400_상태_코드를_반환한다() throws Exception {
      // given
      when(paymentService.createPayment(any(CreatePaymentCommand.class)))
          .thenThrow(CustomException.from(RESERVATION_NOT_PENDING));

      // when
      ResultActions actions = mockMvc.perform(post("/internal/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value(RESERVATION_NOT_PENDING.getMessage()));
    }

    @Test
    void 필수_값이_null이면_400_상태_코드를_반환한다() throws Exception {
      // given
      CreatePaymentRequest invalidRequest = new CreatePaymentRequest(
          null, // reservationUuid is null
          restaurantUuid.toString(),
          customerId,
          reservationName,
          originalAmount
      );

      // when
      ResultActions actions = mockMvc.perform(post("/internal/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest)));

      // then
      actions.andExpect(status().isBadRequest());
    }

    @Test
    void 결제_금액이_null이면_400_상태_코드를_반환한다() throws Exception {
      // given
      CreatePaymentRequest invalidRequest = new CreatePaymentRequest(
          reservationUuid.toString(),
          restaurantUuid.toString(),
          customerId,
          reservationName,
          null // originalAmount is null
      );

      // when
      ResultActions actions = mockMvc.perform(post("/internal/v1/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidRequest)));

      // then
      actions.andExpect(status().isBadRequest());
    }
  }
}