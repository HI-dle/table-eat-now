/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;

@Component
public class ValidTotalPrice implements ValidItem<CreateReservationValidationContext> {

  // 총 결제 금액 검증
  @Override
  public void validate(CreateReservationValidationContext ctx) {
    BigDecimal providedTotal = ctx.command().totalPrice();
    BigDecimal menuTotalPrice = ctx.command().restaurantMenuDetails().price()
        .multiply(BigDecimal.valueOf(ctx.command().restaurantMenuDetails().quantity()));
    BigDecimal paymentsTotal = calculateExpectedTotalAmount(ctx.command().payments());

    validateTotalPrice(providedTotal, paymentsTotal,
        ReservationErrorCode.INVALID_PAYMENT_DETAILS_TOTAL_AMOUNT);

    validateTotalPrice(providedTotal, menuTotalPrice,
        ReservationErrorCode.INVALID_MENU_TOTAL_AMOUNT);
  }

  private static void validateTotalPrice(BigDecimal providedTotal, BigDecimal menuTotalPrice,
      ReservationErrorCode invalidMenuTotalAmount) {
    if (!providedTotal.equals(menuTotalPrice)) {
      throw CustomException.from(invalidMenuTotalAmount);
    }
  }

  private BigDecimal calculateExpectedTotalAmount(
      List<PaymentDetail> payments) {
    return payments.stream()
        .map(CreateReservationCommand.PaymentDetail::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
