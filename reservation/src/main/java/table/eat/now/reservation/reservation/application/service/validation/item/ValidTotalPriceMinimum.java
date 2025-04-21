/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;

@Component
public class ValidTotalPriceMinimum implements ValidItem<CreateReservationValidationContext> {
  @Override
  public void validate(CreateReservationValidationContext context) {
    // 최소 금액 검증
  }
}