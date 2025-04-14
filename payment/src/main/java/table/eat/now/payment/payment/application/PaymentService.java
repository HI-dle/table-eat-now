package table.eat.now.payment.payment.application;

import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;

public interface PaymentService {

  CreatePaymentInfo createPayment(CreatePaymentCommand command);

  GetCheckoutDetailInfo getCheckoutDetail(String idempotencyKey);

  ConfirmPaymentInfo confirmPayment(ConfirmPaymentCommand command);
}
