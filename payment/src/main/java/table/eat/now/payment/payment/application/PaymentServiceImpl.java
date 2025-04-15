package table.eat.now.payment.payment.application;

import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_ACCESS_DENIED;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_PENDING;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.payment.payment.application.client.PgClient;
import table.eat.now.payment.payment.application.client.ReservationClient;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.client.dto.ConfirmPgPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;
import table.eat.now.payment.payment.application.client.dto.GetReservationInfo;
import table.eat.now.payment.payment.application.event.PaymentCanceledEvent;
import table.eat.now.payment.payment.application.event.PaymentCanceledPayload;
import table.eat.now.payment.payment.application.event.PaymentEventPublisher;
import table.eat.now.payment.payment.application.event.PaymentFailedEvent;
import table.eat.now.payment.payment.application.event.PaymentFailedPayload;
import table.eat.now.payment.payment.application.event.PaymentSuccessEvent;
import table.eat.now.payment.payment.application.event.PaymentSuccessPayload;
import table.eat.now.payment.payment.application.helper.TransactionalHelper;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationClient reservationClient;
  private final TransactionalHelper transactionalHelper;
  private final PaymentEventPublisher paymentEventPublisher;
  private final PgClient pgClient;

  @Override
  @Transactional
  public CreatePaymentInfo createPayment(CreatePaymentCommand command) {
    validateReservation(command);
    return CreatePaymentInfo.from(paymentRepository.save(command.toEntity()));
  }

  private void validateReservation(CreatePaymentCommand command) {
    GetReservationInfo reservationInfo = getReservationInfo(command);

    if (reservationInfo.totalAmount().compareTo(command.originalAmount()) != 0) {
      throw CustomException.from(PAYMENT_AMOUNT_MISMATCH);
    }
    if (!reservationInfo.status().equals("PENDING_PAYMENT")) {
      throw CustomException.from(RESERVATION_NOT_PENDING);
    }
  }

  private GetReservationInfo getReservationInfo(CreatePaymentCommand command) {
    return reservationClient.getReservation(command.reservationUuid());
  }

  @Override
  public GetCheckoutDetailInfo getCheckoutDetail(String idempotencyKey) {
    return GetCheckoutDetailInfo.from(getPaymentByIdempotencyKey(idempotencyKey));
  }

  private Payment getPaymentByIdempotencyKey(String idempotencyKey) {
    return paymentRepository
        .findByIdentifier_IdempotencyKeyAndDeletedAtNull(idempotencyKey)
        .orElseThrow(() -> CustomException.from(PAYMENT_NOT_FOUND));
  }

  @Override
  @Transactional
  public ConfirmPaymentInfo confirmPayment(
      ConfirmPaymentCommand command, CurrentUserInfoDto userInfo) {

    Payment payment = getPaymentByReservationId(command.reservationId());
    ConfirmPgPaymentInfo confirmedInfo = confirm(command, payment);
    try {
      completePayment(userInfo, payment, confirmedInfo);
    } catch (Exception e) {
      transactionalHelper.doInNewTransaction(
          () -> rollbackPayment(command, userInfo, e, payment));
    }
    return ConfirmPaymentInfo.from(payment);
  }

  private Payment getPaymentByReservationId(String reservationUuid) {
    return paymentRepository
        .findByReference_ReservationIdAndDeletedAtNull(reservationUuid)
        .orElseThrow(() -> CustomException.from(PAYMENT_NOT_FOUND));
  }

  private ConfirmPgPaymentInfo confirm(ConfirmPaymentCommand command, Payment payment) {
    ConfirmPgPaymentInfo confirmedInfo = pgClient.confirm(command, payment.getIdempotencyKey());
    log.info("Confirm payment {}", confirmedInfo);
    return confirmedInfo;
  }

  private void completePayment(
      CurrentUserInfoDto userInfo, Payment payment, ConfirmPgPaymentInfo confirmedInfo) {
    payment.confirm(confirmedInfo.toConfirm());
    paymentEventPublisher
        .publish(PaymentSuccessEvent.of(PaymentSuccessPayload.from(payment), userInfo));
  }

  private void rollbackPayment(
      ConfirmPaymentCommand command, CurrentUserInfoDto userInfo, Exception e, Payment payment) {
    String cancelReason = e.getMessage();
    cancel(command.paymentKey(), cancelReason, payment);
    paymentEventPublisher.publish(PaymentFailedEvent.of(
        PaymentFailedPayload.from(payment, cancelReason), userInfo
    ));
  }

  private void cancel(String paymentKey, String cancelReason, Payment payment) {
    CancelPgPaymentInfo cancelledInfo = pgClient.cancel(
        CancelPgPaymentCommand.of(paymentKey, cancelReason), payment.getIdempotencyKey());
    log.info("Cancel payment {}", cancelledInfo);
    payment.cancel(cancelledInfo.toCancel());
  }

  @Override
  @Transactional
  public void cancelPayment(CancelPaymentCommand command, CurrentUserInfoDto userInfo) {
    Payment payment = getPaymentByReservationId(command.reservationUuid());
    cancel(command.idempotencyKey(), command.cancelReason(), payment);
    paymentEventPublisher.publish(PaymentCanceledEvent.of(
        PaymentCanceledPayload.from(payment), userInfo
    ));
  }

  @Override
  @Transactional(readOnly = true)
  public GetPaymentInfo getPayment(String paymentUuid, CurrentUserInfoDto userInfo) {
    Payment payment = getPaymentByPaymentId(paymentUuid);
    validateAccess(userInfo, payment);
    return GetPaymentInfo.from(payment);
  }

  private static void validateAccess(CurrentUserInfoDto userInfo, Payment payment) {
    if (userInfo.role() != MASTER &&
        !payment.getReference().getCustomerId().equals(userInfo.userId())) {
      throw CustomException.from(PAYMENT_ACCESS_DENIED);
    }
  }

  private Payment getPaymentByPaymentId(String paymentUuid) {
    return paymentRepository
        .findByIdentifier_PaymentUuidAndDeletedAtNull(paymentUuid)
        .orElseThrow(() -> CustomException.from(PAYMENT_NOT_FOUND));
  }
}
