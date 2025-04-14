package table.eat.now.payment.payment.application.client;

import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.dto.response.CancelPgPaymentInfo;
import table.eat.now.payment.payment.application.dto.response.ConfirmPgPaymentInfo;

public interface PgClient {

  ConfirmPgPaymentInfo confirm(ConfirmPaymentCommand command, String idempotencyKey);

  CancelPgPaymentInfo cancel(CancelPaymentCommand command, String idempotencyKey);
}
