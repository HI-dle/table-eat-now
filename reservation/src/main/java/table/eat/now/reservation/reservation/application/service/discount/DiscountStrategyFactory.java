package table.eat.now.reservation.reservation.application.service.discount;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@RequiredArgsConstructor
public class DiscountStrategyFactory {

  private final Map<String, Coupon> couponMap;
  private final Map<String, GetPromotionsInfo.Promotion> promotions;

  public DiscountStrategy getStrategy(PaymentDetail paymentDetail) {
    return switch (paymentDetail.type()) {
      case PROMOTION_COUPON -> new CouponDiscountStrategy(couponMap);
      case PROMOTION_EVENT -> new PromotionDiscountStrategy(promotions);
      default -> throw CustomException.from(ReservationErrorCode.DISCOUNT_STRATEGY_NOT_FOUND);
    };
  }
}
