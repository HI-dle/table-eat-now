/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.validation.strategy;

import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

public interface PaymentDetailValidationStrategy {

  boolean supports(PaymentDetail paymentDetail);

  void validate();

}
