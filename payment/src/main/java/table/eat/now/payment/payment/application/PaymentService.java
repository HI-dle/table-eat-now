package table.eat.now.payment.payment.application;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;
import table.eat.now.payment.payment.application.dto.response.ConfirmPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.CreatePaymentInfo;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;
import table.eat.now.payment.payment.application.dto.response.GetPaymentInfo;

public interface PaymentService {

  CreatePaymentInfo createPayment(CreatePaymentCommand command);

  GetCheckoutDetailInfo getCheckoutDetail(String idempotencyKey);

  ConfirmPaymentInfo confirmPayment(ConfirmPaymentCommand command, CurrentUserInfoDto userInfo);

  GetPaymentInfo getPayment(String paymentUuid, CurrentUserInfoDto userInfo);
}
