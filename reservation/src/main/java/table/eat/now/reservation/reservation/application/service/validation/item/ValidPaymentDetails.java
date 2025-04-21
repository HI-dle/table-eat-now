/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.discount.DiscountStrategy;
import table.eat.now.reservation.reservation.application.service.validation.discount.DiscountStrategyFactory;

@Component
@RequiredArgsConstructor
public class ValidPaymentDetails implements ValidItem<CreateReservationValidationContext> {

  private final DiscountStrategyFactory strategyFactory;

  @Override
  public void validate(CreateReservationValidationContext ctx) {
    // 사용 쿠폰 수 제한
    if (ctx.command().payments().stream()
        .filter(p -> p.type() == PaymentType.PROMOTION_COUPON).toList().size() > 2) {
      throw CustomException.from(ReservationErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);
    }
    // 사용 프로모션 수 제한
    if (ctx.command().payments().stream()
        .filter(p -> p.type() == PaymentType.PROMOTION_EVENT).toList().size() > 1) {
      throw CustomException.from(ReservationErrorCode.PROMOTION_EVENT_USAGE_LIMIT_EXCEEDED);
    }

    if (ctx.command().payments().stream()
        .filter(p -> p.type() == PaymentType.PAYMENT).toList().size() != 1){
      throw CustomException.from(ReservationErrorCode.PAYMENT_LIMIT_EXCEEDED);
    }

    List<PaymentDetail> payments = ctx.command().payments();

    for (PaymentDetail paymentDetail : payments) {
      if (paymentDetail.type() == PaymentType.PAYMENT) continue;

      DiscountStrategy strategy = strategyFactory.getStrategy(paymentDetail, ctx.couponMap(), ctx.promotionMap());

      strategy.validate(
          ctx.command().totalPrice(),
          paymentDetail,
          ctx.command().reservationDate()
      );
    }
  }
}
