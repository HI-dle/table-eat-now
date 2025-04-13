/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.client;

import table.eat.now.reservation.reservation.application.client.dto.request.CreatePaymentCommand;
import table.eat.now.reservation.reservation.application.client.dto.response.CreatePaymentInfo;

public interface PaymentClient {
  CreatePaymentInfo createPayment(CreatePaymentCommand request);
}
