/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 21.
 */
package table.eat.now.reservation.reservation.application.service.validation.discount;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@Component
@RequiredArgsConstructor
public class DiscountStrategyFactory {

  private final List<DiscountStrategy> strategies;

  public DiscountStrategy getStrategy(
      PaymentDetail paymentDetail,
      Map<String, Coupon> couponMap,
      Map<String, Promotion> promotionsMap
  ) {
    return strategies.stream()
        .filter(strategy -> strategy.supports(paymentDetail))
        .findFirst()
        .map(strategy -> {
          if (strategy instanceof AbstractContextAwareDiscountStrategy s) {
            s.setContext(couponMap, promotionsMap);
          }
          return strategy;
        })
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.DISCOUNT_STRATEGY_NOT_FOUND));
  }
}
