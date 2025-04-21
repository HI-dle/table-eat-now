/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.policy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.service.validation.context.CancelReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.item.ValidItem;

@Component
@RequiredArgsConstructor
public class CancelReservationValidationPolicy implements ValidPolicy<CancelReservationValidationContext> {

  private final List<ValidItem<CancelReservationValidationContext>> items;

  @Override
  public void validate(CancelReservationValidationContext context) {
    for (ValidItem<CancelReservationValidationContext> item : items) {
      item.validate(context);
    }
  }
}
