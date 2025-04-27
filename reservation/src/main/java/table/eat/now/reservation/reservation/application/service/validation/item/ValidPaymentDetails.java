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
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.validation.context.CreateReservationValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.context.PaymentValidationContext;
import table.eat.now.reservation.reservation.application.service.validation.factory.PaymentDetailValidationStrategyFactory;
import table.eat.now.reservation.reservation.application.service.validation.strategy.PaymentDetailValidationStrategy;

@Component
@RequiredArgsConstructor
public class ValidPaymentDetails implements ValidItem<CreateReservationValidationContext> {

  private final PaymentDetailValidationStrategyFactory strategyFactory;
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
        ctx.command().payments(),
        ctx.command().reserverId()
    );
  }

  private static void validateExceededPaymentDetailSize(List<PaymentDetail> paymentDetails,
      PaymentType paymentType, int x, ReservationErrorCode errorCode) {
    if (paymentDetails.stream().filter(p -> p.type() == paymentType).toList().size() > x) {
      throw CustomException.from(errorCode);
    }
  }

  private void validateDiscount(
      BigDecimal totalPrice,
      LocalDateTime reservationDate,
      Map<String, UserCoupon> stringCouponMap,
      Map<String, Promotion> stringPromotionMap,
      List<PaymentDetail> payments,
      Long reserverId
  ) {
    List<PaymentDetail> discountPayments = payments.stream()
        .filter(p -> p.type() != PaymentType.PAYMENT)
        .toList();

    for (PaymentDetail paymentDetail : discountPayments) {
      PaymentValidationContext context = PaymentValidationContext.builder()
          .paymentDetail(paymentDetail)
          .couponMap(stringCouponMap)
          .promotionsMap(stringPromotionMap)
          .totalPrice(totalPrice)
          .reservationDate(reservationDate)
          .reserverId(reserverId)
          .build();

      PaymentDetailValidationStrategy strategy = strategyFactory.getStrategy(context);
      strategy.validate();
    }
  }

}
