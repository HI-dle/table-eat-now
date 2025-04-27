/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.strategy;

import table.eat.now.reservation.reservation.application.service.validation.context.PaymentValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.context.ValidationPaymentDetailContext;

public abstract class AbstractContextAwarePaymentDetailValidationStrategy<T extends ValidationPaymentDetailContext>
    implements PaymentDetailValidationStrategy<T> {

  protected PaymentValidationContext context;

  public void setContext(PaymentValidationContext context) {
    this.context = context;
  }

  @Override
  public void validate() {
    T context = createContext();
    validateContext(context);
  }

  // context 생성 메서드
  protected abstract T createContext();

  // 실제 검증 로직
  protected abstract void validateContext(T context);
}