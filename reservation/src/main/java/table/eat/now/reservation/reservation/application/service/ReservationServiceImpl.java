/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.infrastructure.persistence.JpaReservationRepository;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final JpaReservationRepository reservationRepository;


  @Override
  public CreateReservationInfo createRestaurant(CreateReservationCommand command) {
    String paymentKey = "";
    Reservation saved = reservationRepository.save(command.toEntityWithPaymentKey(paymentKey));
    return CreateReservationInfo.of(saved.getReservationUuid(), paymentKey);
  }
  private void validateCouponDiscounts(GetCouponsInfo couponInfo, CreateReservationCommand command) {
    // 쿠폰 맵 가져오기
    Map<String, GetCouponsInfo.Coupon> couponMap = couponInfo.couponMap();

    // 사용된 쿠폰이 있는지 확인
    List<CreateReservationCommand.PaymentDetail> paymentDetails = command.payments();

    // 쿠폰이 사용되었는지 확인
    for (CreateReservationCommand.PaymentDetail paymentDetail : paymentDetails) {
      if (paymentDetail.type() == CreateReservationCommand.PaymentDetail.PaymentType.PROMOTION_COUPON) {
        String couponUuid = paymentDetail.detailReferenceId();
        GetCouponsInfo.Coupon coupon = couponMap.get(couponUuid);

        if (coupon == null) {
          throw CustomException.from(ReservationErrorCode.COUPON_NOT_FOUND);
        }

        // 쿠폰 유효 기간 검증
        LocalDateTime now = LocalDateTime.now();
        if (coupon.startAt().isAfter(now) || coupon.endAt().isBefore(now)) {
          throw CustomException.from(ReservationErrorCode.COUPON_EXPIRED);
        }

        // 최소 구매 금액 검증
        if (command.totalPrice().compareTo(BigDecimal.valueOf(coupon.minPurchaseAmount())) < 0) {
          throw CustomException.from(ReservationErrorCode.COUPON_MIN_PURCHASE_NOT_MET);
        }

        // 할인 금액 검증
        BigDecimal expectedDiscountAmount = calculateExpectedDiscountAmount(coupon, command.totalPrice());
        BigDecimal actualDiscountAmount = paymentDetail.amount();

        if (expectedDiscountAmount.compareTo(actualDiscountAmount) != 0) {
          throw CustomException.from(ReservationErrorCode.INVALID_COUPON_DISCOUNT);
        }
      }
    }
  }

  // 쿠폰에 따른 예상 할인 금액을 계산하는 로직
  private BigDecimal calculateExpectedDiscountAmount(GetCouponsInfo.Coupon coupon, BigDecimal totalPrice) {
    // 쿠폰의 종류와 할인 방식에 따라 다르게 계산
    if (coupon.type().equals("AMOUNT")) {
      // 정액 할인
      return BigDecimal.valueOf(coupon.amount());
    } else if (coupon.type().equals("PERCENT")) {
      // 퍼센트 할인
      BigDecimal discount = totalPrice.multiply(BigDecimal.valueOf(coupon.percent())).divide(BigDecimal.valueOf(100));
      return discount.min(BigDecimal.valueOf(coupon.maxDiscountAmount())); // 최대 할인 금액을 초과하지 않도록 처리
    }
    // 다른 할인 방식에 대한 처리 추가 가능
    return BigDecimal.ZERO;
  }
  private void validatePromotionDiscounts(GetPromotionsResponse promotionResponse, CreateReservationCommand command) {
    Map<String, PromotionInfo> promotionMap = promotionResponse.toInfo();
    BigDecimal totalDiscount = command.payments().stream()
        .filter(p -> p.type() == CreateReservationCommand.PaymentDetail.PaymentType.PROMOTION_EVENT)
        .map(CreateReservationCommand.PaymentDetail::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal actualDiscount = promotionMap.values().stream()
        .map(PromotionInfo::discountAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (totalDiscount.compareTo(actualDiscount) != 0) {
      throw CustomException.from();
    }
  }
}
