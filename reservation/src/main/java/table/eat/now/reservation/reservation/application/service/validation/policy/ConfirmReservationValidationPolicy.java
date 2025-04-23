package table.eat.now.reservation.reservation.application.service.validation.policy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.service.validation.context.ConfirmReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.item.ValidItem;

@Component
@RequiredArgsConstructor
public class ConfirmReservationValidationPolicy implements ValidPolicy<ConfirmReservationValidationContext> {

  private final List<ValidItem<ConfirmReservationValidationContext>> validItems;

  @Override
  public void validate(ConfirmReservationValidationContext context) {
    for (ValidItem<ConfirmReservationValidationContext> policy : validItems) {
      policy.validate(context);
    }
  }
}
