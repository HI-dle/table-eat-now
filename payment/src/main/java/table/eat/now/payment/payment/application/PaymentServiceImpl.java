package table.eat.now.payment.payment.application;

import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_PENDING;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.payment.payment.application.client.PgClient;
import table.eat.now.payment.payment.application.client.ReservationClient;
import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.CancelPgPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.ConfirmPgPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
import table.eat.now.payment.payment.application.dto.response.GetReservationInfo;
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
  public ConfirmPaymentInfo confirmPayment(ConfirmPaymentCommand command) {
    Payment payment = getPaymentByReservationId(command.reservationId());
    ConfirmPgPaymentInfo confirmedInfo =
        pgClient.confirm(command, payment.getIdempotencyKey());
    log.info("Confirm payment {}", confirmedInfo);
    try {
      payment.confirm(confirmedInfo.toConfirm());
    } catch (IllegalArgumentException e) {
      log.error("Confirm payment failed", e);
      transactionalHelper.doInNewTransaction(()->
        cancelPayment(command, e.getMessage(), payment)
      );
    }
    return ConfirmPaymentInfo.from(payment);
  }

  private Payment getPaymentByReservationId(String reservationUuid) {
    return paymentRepository
        .findByReference_ReservationIdAndDeletedAtNull(reservationUuid)
        .orElseThrow(() -> CustomException.from(PAYMENT_NOT_FOUND));
  }

  @Transactional
  public void cancelPayment(ConfirmPaymentCommand command, String cancelReason, Payment payment) {
    CancelPgPaymentInfo cancelledInfo =
        pgClient.cancel(CancelPaymentCommand.
            of(command.paymentKey(), cancelReason), payment.getIdempotencyKey());
    log.info("Cancel payment {}", cancelledInfo);
    payment.cancel(cancelledInfo.toCancel());
  }
}
