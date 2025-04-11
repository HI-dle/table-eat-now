package table.eat.now.reservation.reservation.application.service.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetCouponsInfo.Coupon;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@RequiredArgsConstructor
public class CouponDiscountStrategy implements DiscountStrategy {

  private final Map<String, Coupon> couponMap;

  @Override
  public void validate(BigDecimal totalPrice, PaymentDetail paymentDetail,
      LocalDateTime reservationDate) {
    Coupon coupon = Optional.ofNullable(couponMap.get(paymentDetail.detailReferenceId()))
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.COUPON_NOT_FOUND));

    // 1. 예약날짜 유효성
    if (reservationDate.isBefore(coupon.startAt()) || reservationDate.isAfter(coupon.endAt())) {
      throw CustomException.from(ReservationErrorCode.COUPON_INVALID_PERIOD);
    }

    // 2. 최소 구매 금액
    if (totalPrice.compareTo(BigDecimal.valueOf(coupon.minPurchaseAmount())) < 0) {
      throw CustomException.from(ReservationErrorCode.COUPON_MIN_PURCHASE_NOT_MET);
    }

    // 3. 할인 금액 검증
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

