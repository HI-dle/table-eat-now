package table.eat.now.payment.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static table.eat.now.common.resolver.dto.UserRole.CUSTOMER;
import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_ACCESS_DENIED;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_PENDING;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.payment.payment.application.client.PgClient;
import table.eat.now.payment.payment.application.client.ReservationClient;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentInfo;
import table.eat.now.payment.payment.application.client.dto.ConfirmPgPaymentInfo;
import table.eat.now.payment.payment.application.client.dto.GetReservationInfo;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;
import table.eat.now.payment.payment.application.event.PaymentCanceledEvent;
import table.eat.now.payment.payment.application.event.PaymentEventPublisher;
import table.eat.now.payment.payment.application.event.PaymentSuccessEvent;
import table.eat.now.payment.payment.application.helper.TransactionalHelper;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.entity.PaymentAmount;
import table.eat.now.payment.payment.domain.entity.PaymentReference;
import table.eat.now.payment.payment.domain.entity.PaymentStatus;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentServiceImplTest {

  @Autowired
  private PaymentService paymentService;

  @MockitoBean
  private PaymentRepository paymentRepository;

  @MockitoBean
  private ReservationClient reservationClient;

  @MockitoBean
  private PgClient pgClient;

  @MockitoBean
  private TransactionalHelper transactionalHelper;

  @MockitoBean
  private PaymentEventPublisher paymentEventPublisher;

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
      assertThat(result.idempotencyKey()).isEqualTo(
          savedPayment.getIdentifier().getIdempotencyKey());
      assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.PENDING.name());
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
          .status("CONFIRMED")
          .totalAmount(originalAmount)
          .build();

      when(reservationClient.getReservation(reservationUuid)).thenReturn(invalidStatusInfo);

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.createPayment(command));

      assertThat(exception.getMessage()).isEqualTo(RESERVATION_NOT_PENDING.getMessage());
    }
  }

  @Nested
  class confirmPayment_는 {

    private String reservationUuid;
    private String paymentKey;
    private BigDecimal totalAmount;
    private ConfirmPaymentCommand command;
    private Payment payment;
    private CurrentUserInfoDto userInfo;

    @BeforeEach
    void setUp() {
      reservationUuid = UUID.randomUUID().toString();
      String restaurantUuid = UUID.randomUUID().toString();
      Long customerId = 123L;
      String reservationName = "테스트 예약";
      paymentKey = "payment_key_123456";
      totalAmount = BigDecimal.valueOf(50000);
      BigDecimal discountAmount = BigDecimal.ZERO;
      userInfo = CurrentUserInfoDto.of(1L, MASTER);

      command = new ConfirmPaymentCommand(reservationUuid, paymentKey, totalAmount);

      PaymentReference reference = PaymentReference.create(
          reservationUuid,
          restaurantUuid,
          customerId,
          reservationName
      );
      PaymentAmount amount = PaymentAmount.create(totalAmount);
      payment = Payment.create(reference, amount);

      LocalDateTime approvedAt = LocalDateTime.now();
      ConfirmPgPaymentInfo confirmPgPaymentInfo = new ConfirmPgPaymentInfo(
          paymentKey,
          discountAmount,
          totalAmount,
          approvedAt
      );

      when(paymentRepository.findByReference_ReservationIdAndDeletedAtNull(reservationUuid))
          .thenReturn(Optional.of(payment));
      when(pgClient.confirm(any(ConfirmPaymentCommand.class), anyString()))
          .thenReturn(confirmPgPaymentInfo);
    }

    @Test
    void 유효한_요청으로_결제를_확인하면_확인된_결제_정보를_반환하고_이벤트를_발송한다() {
      // given
      doNothing().when(paymentEventPublisher).publish(any(PaymentSuccessEvent.class));

      // when
      ConfirmPaymentInfo result = paymentService.confirmPayment(command, userInfo);

      // then
      assertThat(result).isNotNull();
      verify(paymentRepository).findByReference_ReservationIdAndDeletedAtNull(reservationUuid);
      verify(pgClient).confirm(eq(command), anyString());
      verify(paymentEventPublisher).publish(any(PaymentSuccessEvent.class));
    }

    @Test
    void 결제가_존재하지_않으면_예외를_발생시킨다() {
      // given
      when(paymentRepository.findByReference_ReservationIdAndDeletedAtNull(reservationUuid))
          .thenReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.confirmPayment(command, userInfo));

      assertThat(exception.getMessage()).isEqualTo(PAYMENT_NOT_FOUND.getMessage());
      verify(pgClient, never()).confirm(any(), anyString());
    }

    @Test
    void 결제_확인_중_paymentKey가_존재하지_않으면_취소_처리를_한다() {
      // given
      when(paymentRepository.findByReference_ReservationIdAndDeletedAtNull(reservationUuid))
          .thenReturn(Optional.of(payment));

      ConfirmPgPaymentInfo badConfirmInfo = new ConfirmPgPaymentInfo(
          null,
          BigDecimal.ZERO,
          totalAmount,
          LocalDateTime.now()
      );

      when(pgClient.confirm(any(ConfirmPaymentCommand.class), anyString()))
          .thenReturn(badConfirmInfo);

      CancelPgPaymentInfo cancelPgPaymentInfo = new CancelPgPaymentInfo(
          paymentKey,
          "결제 금액 불일치",
          LocalDateTime.now()
      );

      ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

      when(pgClient.cancel(any(CancelPgPaymentCommand.class), anyString()))
          .thenReturn(cancelPgPaymentInfo);

      // when
      paymentService.confirmPayment(command, userInfo);
      // then
      verify(transactionalHelper).doInNewTransaction(runnableCaptor.capture());
      verify(transactionalHelper).doInNewTransaction(any(Runnable.class));
      runnableCaptor.getValue().run();
      verify(pgClient).cancel(any(CancelPgPaymentCommand.class), anyString());
    }
  }

  @Nested
  class getCheckoutDetail_은 {

    private String idempotencyKey;

    @BeforeEach
    void setUp() {
      String reservationUuid = UUID.randomUUID().toString();
      String restaurantUuid = UUID.randomUUID().toString();
      Long customerId = 123L;
      String reservationName = "테스트 예약";
      BigDecimal originalAmount = BigDecimal.valueOf(10000);

      PaymentReference reference = PaymentReference.create(
          reservationUuid,
          restaurantUuid,
          customerId,
          reservationName
      );
      PaymentAmount amount = PaymentAmount.create(originalAmount);
      Payment payment = Payment.create(reference, amount);
      idempotencyKey = payment.getIdempotencyKey();

      when(paymentRepository.findByIdentifier_IdempotencyKeyAndDeletedAtNull(idempotencyKey))
          .thenReturn(Optional.of(payment));
    }

    @Test
    void 유효한_idempotencyKey로_체크아웃_정보를_조회하면_정보를_반환한다() {
      // when
      paymentService.getCheckoutDetail(idempotencyKey);

      // then
      verify(paymentRepository).findByIdentifier_IdempotencyKeyAndDeletedAtNull(idempotencyKey);
    }

    @Test
    void 결제가_존재하지_않으면_예외를_발생시킨다() {
      // given
      when(paymentRepository.findByIdentifier_IdempotencyKeyAndDeletedAtNull(idempotencyKey))
          .thenReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.getCheckoutDetail(idempotencyKey));

      assertThat(exception.getMessage()).isEqualTo(PAYMENT_NOT_FOUND.getMessage());
    }
  }

  @Nested
  class cancelPayment_는 {

    private String reservationUuid;
    private String cancelReason;
    private Payment payment;
    private CurrentUserInfoDto userInfo;
    private CancelPaymentCommand command;

    @BeforeEach
    void setUp() {
      reservationUuid = UUID.randomUUID().toString();
      String restaurantUuid = UUID.randomUUID().toString();
      Long customerId = 123L;
      String reservationName = "테스트 예약";
      String paymentKey = "payment_key_123456";
      BigDecimal totalAmount = BigDecimal.valueOf(50000);
      cancelReason = "고객 요청으로 인한 취소";
      userInfo = CurrentUserInfoDto.of(customerId, CUSTOMER);
      String idempotencyKey = UUID.randomUUID().toString();

      command = new CancelPaymentCommand(
          reservationUuid,
          idempotencyKey,
          cancelReason
      );

      PaymentReference reference = PaymentReference.create(
          reservationUuid,
          restaurantUuid,
          customerId,
          reservationName
      );
      PaymentAmount amount = PaymentAmount.create(totalAmount);
      payment = Payment.create(reference, amount);

      CancelPgPaymentInfo cancelPgPaymentInfo = new CancelPgPaymentInfo(
          paymentKey,
          cancelReason,
          LocalDateTime.now()
      );

      when(paymentRepository.findByReference_ReservationIdAndDeletedAtNull(reservationUuid))
          .thenReturn(Optional.of(payment));
      when(pgClient.cancel(any(CancelPgPaymentCommand.class), anyString()))
          .thenReturn(cancelPgPaymentInfo);
      doNothing().when(paymentEventPublisher).publish(any(PaymentCanceledEvent.class));
    }

    @Test
    void 유효한_요청으로_결제를_취소하면_PG사_취소_처리를_하고_결제_상태를_변경한다() {
      // when
      paymentService.cancelPayment(command, userInfo);

      // then
      ArgumentCaptor<CancelPgPaymentCommand> commandCaptor = ArgumentCaptor.forClass(
          CancelPgPaymentCommand.class);
      verify(pgClient).cancel(commandCaptor.capture(), eq(payment.getIdempotencyKey()));

      CancelPgPaymentCommand capturedCommand = commandCaptor.getValue();
      assertThat(capturedCommand.cancelReason()).isEqualTo(cancelReason);

      verify(paymentEventPublisher).publish(any(PaymentCanceledEvent.class));
    }

    @Test
    void 결제가_존재하지_않으면_예외를_발생시킨다() {
      // given
      when(paymentRepository.findByReference_ReservationIdAndDeletedAtNull(reservationUuid))
          .thenReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.cancelPayment(command, userInfo));

      assertThat(exception.getMessage()).isEqualTo(PAYMENT_NOT_FOUND.getMessage());
      verify(pgClient, never()).cancel(any(), anyString());
      verify(paymentEventPublisher, never()).publish(any(PaymentCanceledEvent.class));
    }

    @Test
    void 결제_취소_후_이벤트가_발행된다() {
      // when
      paymentService.cancelPayment(command, userInfo);

      // then
      ArgumentCaptor<PaymentCanceledEvent> eventCaptor = ArgumentCaptor.forClass(
          PaymentCanceledEvent.class);
      verify(paymentEventPublisher).publish(eventCaptor.capture());

      PaymentCanceledEvent capturedEvent = eventCaptor.getValue();
      assertThat(capturedEvent.paymentUuid()).isEqualTo(payment.getIdentifier().getPaymentUuid());
      assertThat(capturedEvent.eventType().name()).isEqualTo("CANCEL_SUCCEED");
      assertThat(capturedEvent.userInfo()).isEqualTo(userInfo);
    }
  }

  @Nested
  class getPayment_는 {

    private String paymentUuid;
    private Payment payment;
    private CurrentUserInfoDto userInfo;
    private CurrentUserInfoDto otherUserInfo;
    private CurrentUserInfoDto masterUserInfo;

    @BeforeEach
    void setUp() {
      // 필요한 데이터 준비
      paymentUuid = UUID.randomUUID().toString();
      String reservationUuid = UUID.randomUUID().toString();
      String restaurantUuid = UUID.randomUUID().toString();
      Long customerId = 123L;
      String reservationName = "테스트 예약";
      BigDecimal originalAmount = BigDecimal.valueOf(15000);

      // User Info 설정
      userInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      otherUserInfo = new CurrentUserInfoDto(456L, CUSTOMER);
      masterUserInfo = new CurrentUserInfoDto(789L, MASTER);

      // 결제 객체 생성
      PaymentReference reference = PaymentReference.create(
          reservationUuid,
          restaurantUuid,
          customerId,
          reservationName
      );
      PaymentAmount amount = PaymentAmount.create(originalAmount);
      payment = Payment.create(reference, amount);

      // 레포지토리 모킹
      when(paymentRepository.findByIdentifier_PaymentUuidAndDeletedAtNull(paymentUuid))
          .thenReturn(Optional.of(payment));
    }

    @Test
    void 결제_소유자가_조회하면_결제_정보를_반환한다() {
      // when
      GetPaymentInfo result = paymentService.getPayment(paymentUuid, userInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.paymentUuid()).isEqualTo(payment.getIdentifier().getPaymentUuid());
      assertThat(result.customerId()).isEqualTo(payment.getReference().getCustomerId());
      assertThat(result.reservationId()).isEqualTo(payment.getReference().getReservationId());
      assertThat(result.restaurantId()).isEqualTo(payment.getReference().getRestaurantId());
      assertThat(result.reservationName()).isEqualTo(payment.getReference().getReservationName());
      assertThat(result.paymentStatus()).isEqualTo(payment.getPaymentStatus().name());
      assertThat(result.originalAmount()).isEqualTo(payment.getAmount().getOriginalAmount());

      // 메서드 호출 검증
      verify(paymentRepository).findByIdentifier_PaymentUuidAndDeletedAtNull(paymentUuid);
    }

    @Test
    void 마스터_권한으로_조회하면_결제_정보를_반환한다() {
      // when
      GetPaymentInfo result = paymentService.getPayment(paymentUuid, masterUserInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.paymentUuid()).isEqualTo(payment.getIdentifier().getPaymentUuid());
      // 필요한 필드 검증
      assertThat(result.customerId()).isEqualTo(payment.getReference().getCustomerId());

      // 메서드 호출 검증
      verify(paymentRepository).findByIdentifier_PaymentUuidAndDeletedAtNull(paymentUuid);
    }

    @Test
    void 다른_사용자가_조회하면_접근_거부_예외를_발생시킨다() {
      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.getPayment(paymentUuid, otherUserInfo));

      assertThat(exception.getMessage()).isEqualTo(PAYMENT_ACCESS_DENIED.getMessage());

      // 메서드 호출 검증
      verify(paymentRepository).findByIdentifier_PaymentUuidAndDeletedAtNull(paymentUuid);
    }

    @Test
    void 존재하지_않는_결제를_조회하면_예외를_발생시킨다() {
      // given
      String nonExistentPaymentUuid = UUID.randomUUID().toString();
      when(paymentRepository.findByIdentifier_PaymentUuidAndDeletedAtNull(nonExistentPaymentUuid))
          .thenReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          paymentService.getPayment(nonExistentPaymentUuid, userInfo));

      assertThat(exception.getMessage()).isEqualTo(PAYMENT_NOT_FOUND.getMessage());

      // 메서드 호출 검증
      verify(paymentRepository).findByIdentifier_PaymentUuidAndDeletedAtNull(
          nonExistentPaymentUuid);
    }
  }
}