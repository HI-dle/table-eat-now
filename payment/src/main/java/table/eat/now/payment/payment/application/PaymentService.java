package table.eat.now.payment.payment.application;

import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;

public interface PaymentService {

  CreatePaymentInfo createPayment(CreatePaymentCommand command);
}
