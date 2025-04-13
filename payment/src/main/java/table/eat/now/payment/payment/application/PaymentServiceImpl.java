package table.eat.now.payment.payment.application;

import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_PENDING;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.common.exception.CustomException;
import table.eat.now.payment.payment.application.client.ReservationClient;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetReservationInfo;
import table.eat.now.payment.payment.domain.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationClient reservationClient;

  @Override
  public CreatePaymentInfo createPayment(CreatePaymentCommand command) {
    validateReservation(command);
    return CreatePaymentInfo.from(paymentRepository.save(command.toEntity()));
  }

  private void validateReservation(CreatePaymentCommand command) {
    GetReservationInfo reservationInfo = getReservationInfo(command);

    if (reservationInfo.totalAmount().compareTo(command.originalAmount()) != 0) {
      throw CustomException.from(PAYMENT_AMOUNT_MISMATCH);
    }
    if (!reservationInfo.status().equals("PENDING_PAYMENT")){
      throw CustomException.from(RESERVATION_NOT_PENDING);
    }
  }

  private GetReservationInfo getReservationInfo(CreatePaymentCommand command) {
    return reservationClient.getReservation(command.reservationUuid());
  }
}
