/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@RequiredArgsConstructor
public class PromotionDiscountStrategy implements DiscountStrategy {

  private final Map<String, Promotion> promotions;

  @Override
  public void validate(BigDecimal totalPrice, PaymentDetail paymentDetail, LocalDateTime reservationDate) {
    var promotion = Optional.ofNullable(promotions.get(paymentDetail.detailReferenceId()))
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.PROMOTION_NOT_FOUND));

    // 1. 상태 확인
    if (promotion.promotionStatus() != GetPromotionsInfo.Promotion.PromotionStatus.RUNNING) {
      throw CustomException.from(ReservationErrorCode.PROMOTION_INVALID_RUNNING);
    }

    // 2. 할인 금액 확인
    BigDecimal expected = promotion.discountPrice();
    if (expected.compareTo(paymentDetail.amount()) != 0) {
      throw CustomException.from(ReservationErrorCode.INVALID_PROMOTION_DISCOUNT);
    }
  }
}

