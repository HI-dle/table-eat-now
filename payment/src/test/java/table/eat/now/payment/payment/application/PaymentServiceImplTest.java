package table.eat.now.payment.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_PENDING;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.payment.payment.application.client.ReservationClient;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetReservationInfo;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.entity.PaymentStatus;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PaymentServiceImplTest {

  @Autowired
  private PaymentService paymentService;

  @MockitoBean
  private PaymentRepository paymentRepository;

  @MockitoBean
  private ReservationClient reservationClient;

  @Nested
  class createPayment_는 {

    private String reservationUuid;
    private String restaurantUuid;
    private Long customerId;
    private String reservationName;
    private BigDecimal originalAmount;
    private CreatePaymentCommand command;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
      reservationUuid = UUID.randomUUID().toString();
      restaurantUuid = UUID.randomUUID().toString();
      customerId = 123L;
      reservationName = "고객님의 예약";
      originalAmount = BigDecimal.valueOf(50000);

      command = CreatePaymentCommand.builder()
          .reservationUuid(reservationUuid)
          .restaurantUuid(restaurantUuid)
          .customerId(customerId)
          .reservationName(reservationName)
          .originalAmount(originalAmount)
          .build();

      GetReservationInfo reservationInfo = GetReservationInfo.builder()
          .reservationUuid(reservationUuid)
          .restaurantUuid(restaurantUuid)
          .customerId(customerId)
          .status("PENDING_PAYMENT")
          .totalAmount(originalAmount)
          .build();

      savedPayment = command.toEntity();
      when(reservationClient.getReservation(reservationUuid)).thenReturn(reservationInfo);
      when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
    }

    @Test
    void 유효한_요청으로_결제를_생성하면_저장된_결제_정보를_반환한다() {
      // when
      CreatePaymentInfo result = paymentService.createPayment(command);

      // then
      assertThat(result).isNotNull();
      assertThat(result.paymentUuid()).isEqualTo(savedPayment.getIdentifier().getPaymentUuid());
      assertThat(result.idempotencyKey()).isEqualTo(savedPayment.getIdentifier().getIdempotencyKey());
      assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.CREATED.name());
      assertThat(result.originalAmount()).isEqualTo(originalAmount);

      verify(reservationClient).getReservation(reservationUuid);
      verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제_금액이_예약_금액과_일치하지_않으면_예외를_발생시킨다() {
      // given
      BigDecimal differentAmount = BigDecimal.valueOf(60000);
      CreatePaymentCommand invalidCommand = CreatePaymentCommand.builder()
          .reservationUuid(reservationUuid)
          .restaurantUuid(restaurantUuid)
          .customerId(customerId)
          .reservationName(reservationName)
          .originalAmount(differentAmount)
          .build();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.createPayment(invalidCommand));

      assertThat(exception.getMessage()).isEqualTo(PAYMENT_AMOUNT_MISMATCH.getMessage());
    }

    @Test
    void 예약_상태가_결제_대기_상태가_아니면_예외를_발생시킨다() {
      // given
      GetReservationInfo invalidStatusInfo = GetReservationInfo.builder()
          .reservationUuid(reservationUuid)
          .restaurantUuid(restaurantUuid)
          .customerId(customerId)
          .status("CONFIRMED") // 결제 대기 상태가 아님
          .totalAmount(originalAmount)
          .build();

      when(reservationClient.getReservation(reservationUuid)).thenReturn(invalidStatusInfo);

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.createPayment(command));

      assertThat(exception.getMessage()).isEqualTo(RESERVATION_NOT_PENDING.getMessage());
    }
  }
}