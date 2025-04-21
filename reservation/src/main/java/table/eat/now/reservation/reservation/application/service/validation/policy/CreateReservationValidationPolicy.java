/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.policy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.item.ValidItem;

@Component
@RequiredArgsConstructor
public class CreateReservationValidationPolicy implements ValidPolicy<CreateReservationValidationContext> {

  private final List<ValidItem<CreateReservationValidationContext>> policies;

  @Override
  public void validate(CreateReservationValidationContext context) {
    for (ValidItem<CreateReservationValidationContext> policy : policies) {
      policy.validate(context);
    }
  }
}
