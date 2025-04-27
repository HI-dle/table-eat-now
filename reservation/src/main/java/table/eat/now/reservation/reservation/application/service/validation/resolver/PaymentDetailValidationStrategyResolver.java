/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.resolver;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.validation.context.PaymentValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.strategy.AbstractContextAwarePaymentDetailValidationStrategy;
import table.eat.now.reservation.reservation.application.service.validation.strategy.PaymentDetailValidationStrategy;

@Component
@RequiredArgsConstructor
public class PaymentDetailValidationStrategyResolver {

  private final List<PaymentDetailValidationStrategy> strategies;

  public PaymentDetailValidationStrategy getStrategy(
      PaymentValidationContext context
  ) {
    return strategies.stream()
        .filter(strategy -> strategy.supports(context.paymentDetail()))
        .findFirst()
        .map(strategy -> {
          if (strategy instanceof AbstractContextAwarePaymentDetailValidationStrategy s) {
            s.setContext(context); // ValidationContext를 전달
          }
          return strategy;
        })
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.DISCOUNT_STRATEGY_NOT_FOUND));
  }

}
