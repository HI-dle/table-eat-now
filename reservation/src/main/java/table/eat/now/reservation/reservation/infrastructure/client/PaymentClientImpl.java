/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.client.PaymentClient;
import table.eat.now.reservation.reservation.application.client.dto.request.CreatePaymentCommand;
import table.eat.now.reservation.reservation.application.client.dto.response.CreatePaymentInfo;
import table.eat.now.reservation.reservation.infrastructure.client.feign.PaymentFeignClient;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request.CreatePaymentRequest;

@Component
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {

  private final PaymentFeignClient paymentFeignClient;

  @Override
  public CreatePaymentInfo createPayment(CreatePaymentCommand command) {
    return paymentFeignClient.createPayment(CreatePaymentRequest.from(command)).getBody().toInfo();
  }
}
