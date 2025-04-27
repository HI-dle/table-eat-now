/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.validation.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon.UserCouponStatus;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.validation.context.CouponValidationContext;

@Component
@RequiredArgsConstructor
public class CouponValidationStrategy extends
    AbstractContextAwarePaymentDetailValidationStrategy<CouponValidationContext> {

  @Override
  public boolean supports(PaymentDetail paymentDetail) {
    return paymentDetail.type() == PaymentType.PROMOTION_COUPON;
  }

  @Override
  public CouponValidationContext createContext() {
    return CouponValidationContext.builder()
        .paymentDetail(context.paymentDetail())
        .couponMap(context.couponMap())
        .totalPrice(context.totalPrice())
        .reservationDate(context.reservationDate())
        .reserverId(context.reserverId())
        .build();
  }

  @Override
  public void validateContext(CouponValidationContext context) {
    UserCoupon userCoupon = Optional.ofNullable(context.couponMap().get(context.paymentDetail().detailReferenceId()))
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.USERCOUPON_NOT_FOUND));

    // 예약날짜 유효성
    validateCouponPeriod(context.reservationDate(), userCoupon);

    // 최소 구매 금액
    validateMinPurchaseAmount(context.totalPrice(), userCoupon);

    // 할인 금액 검증
    validateDiscountAmount(context.totalPrice(), context.paymentDetail(), userCoupon);

    // 예약자 확인
    validateReserverId(context.reserverId(), userCoupon.userId());

    // 유저 쿠폰 상태 확인
    validateUserCouponStatus(userCoupon.status());
  }

  private static void validateCouponPeriod(LocalDateTime reservationDate, UserCoupon userCoupon) {
    boolean isExpired = reservationDate.isAfter(userCoupon.expiresAt());
    if (isExpired) {
      throw CustomException.from(ReservationErrorCode.USERCOUPON_EXPIRED);
    }
  }

  private static void validateMinPurchaseAmount(BigDecimal totalPrice, UserCoupon userCoupon) {
    boolean isTotalPriceUnderThanMinPurchaseAmount
        = totalPrice.compareTo(BigDecimal.valueOf(userCoupon.coupon().minPurchaseAmount())) < 0;
    if (isTotalPriceUnderThanMinPurchaseAmount) {
      throw CustomException.from(ReservationErrorCode.COUPON_MIN_PURCHASE_NOT_MET);
    }
  }

  private static void validateDiscountAmount(BigDecimal totalPrice, PaymentDetail paymentDetail, UserCoupon userCoupon) {
    BigDecimal expectedDiscount;
    switch (userCoupon.coupon().type()) {
      case PERCENT_DISCOUNT -> {
        BigDecimal percentDiscount = totalPrice.multiply(BigDecimal.valueOf(userCoupon.coupon().percent()))
            .divide(BigDecimal.valueOf(100));
        BigDecimal maxDiscount = BigDecimal.valueOf(userCoupon.coupon().maxDiscountAmount());
        expectedDiscount = percentDiscount.min(maxDiscount);
      }

      case FIXED_DISCOUNT -> expectedDiscount = BigDecimal.valueOf(userCoupon.coupon().amount());

      default -> throw CustomException.from(ReservationErrorCode.COUPON_TYPE_NOT_FOUND);
    }

    if (expectedDiscount.compareTo(paymentDetail.amount()) != 0) {
      throw CustomException.from(ReservationErrorCode.INVALID_COUPON_DISCOUNT);
    }
  }

  private static void validateReserverId(Long reserverId, Long userCouponUserId) {
    if(!reserverId.equals(userCouponUserId)) {
      throw CustomException.from(ReservationErrorCode.COUPON_USE_PERMISSION);
    }
  }

  private static void validateUserCouponStatus(UserCouponStatus status) {
    if(status == UserCouponStatus.ISSUED || status == UserCouponStatus.ROLLBACK) return;
    throw CustomException.from(ReservationErrorCode.INVALID_USERCOUPON_STATUS_FOR_RESERVATION);
  }
}

