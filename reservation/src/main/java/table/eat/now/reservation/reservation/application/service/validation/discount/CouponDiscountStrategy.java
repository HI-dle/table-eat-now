/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.validation.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;

@Component
@RequiredArgsConstructor
public class CouponDiscountStrategy extends AbstractContextAwareDiscountStrategy {

  @Override
  public boolean supports(PaymentDetail paymentDetail) {
    return paymentDetail.type() == PaymentType.PROMOTION_COUPON;
  }

  @Override
  public void validate(BigDecimal totalPrice, PaymentDetail paymentDetail,
      LocalDateTime reservationDate) {
    Coupon coupon = Optional.ofNullable(couponMap.get(paymentDetail.detailReferenceId()))
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.COUPON_NOT_FOUND));

    // 1. 예약날짜 유효성
    validateCouponPeriod(reservationDate, coupon);

    // 2. 최소 구매 금액
    validateMinPurchaseAmount(totalPrice, coupon);

    // 3. 할인 금액 검증
    validateDiscountAmount(totalPrice, paymentDetail, coupon);
  }

  private static void validateCouponPeriod(LocalDateTime reservationDate, Coupon coupon) {
    boolean isOutOfCouponPeriod =
        reservationDate.isBefore(coupon.startAt()) || reservationDate.isAfter(coupon.endAt());
    if (isOutOfCouponPeriod) {
      throw CustomException.from(ReservationErrorCode.COUPON_INVALID_PERIOD);
    }
  }

  private static void validateMinPurchaseAmount(BigDecimal totalPrice, Coupon coupon) {
    boolean isTotalPriceUnderThanMinPurchaseAmount
        = totalPrice.compareTo(BigDecimal.valueOf(coupon.minPurchaseAmount())) < 0;
    if (isTotalPriceUnderThanMinPurchaseAmount) {
      throw CustomException.from(ReservationErrorCode.COUPON_MIN_PURCHASE_NOT_MET);
    }
  }

  private static void validateDiscountAmount(BigDecimal totalPrice, PaymentDetail paymentDetail, Coupon coupon) {
    BigDecimal expectedDiscount;
    switch (coupon.type()) {
      case PERCENT_DISCOUNT -> {
        BigDecimal percentDiscount = totalPrice.multiply(BigDecimal.valueOf(coupon.percent()))
            .divide(BigDecimal.valueOf(100));
        BigDecimal maxDiscount = BigDecimal.valueOf(coupon.maxDiscountAmount());
        expectedDiscount = percentDiscount.min(maxDiscount);
      }

      case FIXED_DISCOUNT -> expectedDiscount = BigDecimal.valueOf(coupon.amount());

      default -> throw CustomException.from(ReservationErrorCode.COUPON_TYPE_NOT_FOUND);
    }

    if (expectedDiscount.compareTo(paymentDetail.amount()) != 0) {
      throw CustomException.from(ReservationErrorCode.INVALID_COUPON_DISCOUNT);
    }
  }
}

