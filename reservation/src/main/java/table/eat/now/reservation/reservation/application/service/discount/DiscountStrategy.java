/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

public interface DiscountStrategy {
  void validate(BigDecimal totalPrice, PaymentDetail paymentDetail, LocalDateTime reservationDate);
}
