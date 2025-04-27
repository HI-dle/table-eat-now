/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.validation.strategy;

import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.validation.context.ValidationPaymentDetailContext;

public interface PaymentDetailValidationStrategy<T extends ValidationPaymentDetailContext> {

  boolean supports(PaymentDetail paymentDetail);

  void validate();

}
