package table.eat.now.payment.payment.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.client.PgClient;
import table.eat.now.payment.payment.application.dto.request.CancelPaymentCommand;
import table.eat.now.payment.payment.application.dto.request.ConfirmPaymentCommand;
import table.eat.now.payment.payment.application.client.dto.CancelPgPaymentInfo;
import table.eat.now.payment.payment.application.client.dto.ConfirmPgPaymentInfo;
import table.eat.now.payment.payment.infrastructure.client.dto.request.CancelTossPayRequest;
import table.eat.now.payment.payment.infrastructure.client.dto.request.ConfirmTossPayRequest;
import table.eat.now.payment.payment.infrastructure.client.pg.TossPaymentClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgClientImpl implements PgClient {

  private final TossPaymentClient tossPaymentClient;

  @Override
  public ConfirmPgPaymentInfo confirm(ConfirmPaymentCommand command, String idempotencyKey) {
    return tossPaymentClient
        .confirmPayment(ConfirmTossPayRequest.from(command), idempotencyKey).toInfo();
  }

  @Override
  public CancelPgPaymentInfo cancel(CancelPaymentCommand command, String idempotencyKey) {
    return tossPaymentClient
        .cancelPayment(
            CancelTossPayRequest.from(command),
            idempotencyKey,
            command.paymentKey()).toInfo();
  }
}
