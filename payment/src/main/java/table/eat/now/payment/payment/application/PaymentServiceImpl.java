package table.eat.now.payment.payment.application;

import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.CANCEL_AMOUNT_EXCEED_BALANCE;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_ACCESS_DENIED;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_ALREADY_CANCELLED;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static table.eat.now.payment.payment.domain.entity.PaymentStatus.CANCELED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.payment.payment.application.client.PgClient;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentCommand;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentInfo;
import table.eat.now.payment.payment.application.client.dto.ConfirmPgPaymentInfo;
import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.request.SearchMasterPaymentsQuery;
import table.eat.now.payment.payment.application.dto.request.SearchMyPaymentsQuery;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.PaginatedInfo;
import table.eat.now.payment.payment.application.dto.response.SearchPaymentsInfo;
import table.eat.now.payment.payment.application.event.PaymentEventPublisher;
import table.eat.now.payment.payment.application.event.ReservationPaymentCancelledEvent;
import table.eat.now.payment.payment.application.event.ReservationPaymentCancelledPayload;
import table.eat.now.payment.payment.application.event.ReservationPaymentSucceedEvent;
import table.eat.now.payment.payment.application.event.ReservationPaymentSucceedPayload;
import table.eat.now.payment.payment.application.metric.MetricName;
import table.eat.now.payment.payment.application.metric.RecordCount;
import table.eat.now.payment.payment.domain.entity.Payment;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentEventPublisher paymentEventPublisher;
  private final PgClient pgClient;

  @Override
  @Transactional
  public CreatePaymentInfo createPayment(CreatePaymentCommand command) {
    return CreatePaymentInfo.from(paymentRepository.save(command.toEntity()));
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
  @RecordCount(name = MetricName.PAYMENT_SERVICE_CANCEL)
  public ConfirmPaymentInfo confirmPayment(
      ConfirmPaymentCommand command, CurrentUserInfoDto userInfo) {
    Payment payment = getPaymentByReservationId(command.reservationId());
    ConfirmPgPaymentInfo confirmedInfo = pgClient.confirm(command, payment.getIdempotencyKey());
    withIllegalException(() -> payment.confirm(confirmedInfo.toConfirm()));
    paymentEventPublisher
        .publish(ReservationPaymentSucceedEvent.of(
            ReservationPaymentSucceedPayload.from(payment), userInfo));

    return ConfirmPaymentInfo.from(payment);
  }

  private Payment getPaymentByReservationId(String reservationUuid) {
    return paymentRepository
        .findByReference_ReservationIdAndDeletedAtNull(reservationUuid)
        .orElseThrow(() -> CustomException.from(PAYMENT_NOT_FOUND));
  }

  private void withIllegalException(Runnable runnable) {
    try {
      runnable.run();
    } catch (IllegalArgumentException e) {
      log.warn("Illegal argument: {}", e.getMessage());
    }
  }

  @Override
  @Transactional
  @RecordCount(name = MetricName.PAYMENT_SERVICE_CANCEL)
  public void cancelPayment(CancelPaymentCommand command, CurrentUserInfoDto userInfo) {
    Payment payment = getPaymentByReservationId(command.reservationUuid());
    validatePaymentRequest(command, payment);
    CancelPgPaymentInfo cancelledInfo = pgClient.cancel(
        CancelPgPaymentCommand.of(
            payment.getPaymentKey(),
            command.cancelReason(),
            command.cancelAmount()
        ),
        payment.getIdempotencyKey()
    );
    log.debug("Cancel payment {}", cancelledInfo);
    withIllegalException(() -> payment.cancel(cancelledInfo.toCancel()));
    paymentEventPublisher.publish(ReservationPaymentCancelledEvent.of(
        ReservationPaymentCancelledPayload.from(payment), userInfo
    ));
  }

  private static void validatePaymentRequest(CancelPaymentCommand command, Payment payment) {
    if (payment.getBalancedAmount().compareTo(command.cancelAmount()) < 0) {
      throw CustomException.from(CANCEL_AMOUNT_EXCEED_BALANCE);
    }
    if (payment.getPaymentStatus() == CANCELED) {
      throw CustomException.from(PAYMENT_ALREADY_CANCELLED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public GetPaymentInfo getPayment(String paymentUuid, CurrentUserInfoDto userInfo) {
    Payment payment = getPaymentByPaymentId(paymentUuid);
    validateAccess(userInfo, payment);
    return GetPaymentInfo.from(payment);
  }

  private Payment getPaymentByPaymentId(String paymentUuid) {
    return paymentRepository
        .findByIdentifier_PaymentUuidAndDeletedAtNull(paymentUuid)
        .orElseThrow(() -> CustomException.from(PAYMENT_NOT_FOUND));
  }

  private static void validateAccess(CurrentUserInfoDto userInfo, Payment payment) {
    if (userInfo.role() != MASTER &&
        !payment.getReference().getCustomerId().equals(userInfo.userId())) {
      throw CustomException.from(PAYMENT_ACCESS_DENIED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedInfo<SearchPaymentsInfo> searchMyPayments(SearchMyPaymentsQuery query) {
    return PaginatedInfo.from(
            paymentRepository.searchPayments(query.toCriteria()))
        .map(SearchPaymentsInfo::from);
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedInfo<SearchPaymentsInfo> searchMasterPayments(SearchMasterPaymentsQuery query) {
    return PaginatedInfo.from(
            paymentRepository.searchPayments(query.toCriteria()))
        .map(SearchPaymentsInfo::from);
  }
}
