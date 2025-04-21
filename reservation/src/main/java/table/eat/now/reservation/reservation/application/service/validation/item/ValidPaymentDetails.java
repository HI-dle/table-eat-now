/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
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
  private static final int MAX_COUPON_USAGE = 2;
  private static final int MAX_PROMOTION_USAGE = 1;
  private static final int MAX_PAYMENT_USAGE = 1;

  @Override
  public void validate(CreateReservationValidationContext ctx) {
    // 사용 쿠폰 수 제한
    validateExceededPaymentDetailSize(
        ctx.command().payments(), PaymentType.PROMOTION_COUPON, MAX_COUPON_USAGE,
        ReservationErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);

    // 사용 프로모션 수 제한
    validateExceededPaymentDetailSize(
        ctx.command().payments(), PaymentType.PROMOTION_EVENT, MAX_PROMOTION_USAGE,
        ReservationErrorCode.PROMOTION_EVENT_USAGE_LIMIT_EXCEEDED);

    validateExceededPaymentDetailSize(
        ctx.command().payments(), PaymentType.PAYMENT, MAX_PAYMENT_USAGE,
        ReservationErrorCode.PAYMENT_LIMIT_EXCEEDED);

    validateDiscount(
        ctx.command().totalPrice(),
        ctx.command().reservationDate(),
        ctx.couponMap(),
        ctx.promotionMap(),
        ctx.command().payments()
    );
  }

  private static void validateExceededPaymentDetailSize(List<PaymentDetail> ctx,
      PaymentType promotionEvent, int x, ReservationErrorCode promotionEventUsageLimitExceeded) {
    if (ctx.stream()
        .filter(p -> p.type() == promotionEvent).toList().size() > x) {
      throw CustomException.from(promotionEventUsageLimitExceeded);
    }
  }

  private void validateDiscount(
      BigDecimal totalPrice,
      LocalDateTime reservationDate,
      Map<String, Coupon> stringCouponMap,
      Map<String, Promotion> stringPromotionMap,
      List<PaymentDetail> payments
  ) {
    for (PaymentDetail paymentDetail : payments) {
      if (paymentDetail.type() == PaymentType.PAYMENT) continue;

      DiscountStrategy strategy = strategyFactory
          .getStrategy(paymentDetail, stringCouponMap, stringPromotionMap);

      strategy.validate(
          totalPrice,
          paymentDetail,
          reservationDate
      );
    }
  }

}
