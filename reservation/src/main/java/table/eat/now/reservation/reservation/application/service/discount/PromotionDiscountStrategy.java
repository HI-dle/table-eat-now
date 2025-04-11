package table.eat.now.reservation.reservation.application.service.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@RequiredArgsConstructor
public class PromotionDiscountStrategy implements DiscountStrategy {

  private final Map<String, Promotion> promotions;

  @Override
  public void validate(BigDecimal totalPrice, PaymentDetail paymentDetail, LocalDateTime reservationDate) {
    var promotion = Optional.ofNullable(promotions.get(paymentDetail.detailReferenceId()))
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 프로모션입니다."));

    // 1. 예약 날짜 유효성
    if (reservationDate.isBefore(promotion.startTime()) || reservationDate.isAfter(promotion.endTime())) {
      throw new IllegalArgumentException("프로모션 적용 기간이 아닙니다.");
    }

    // 2. 상태 확인
    if (promotion.promotionStatus() != GetPromotionsInfo.Promotion.PromotionStatus.RUNNING) {
      throw new IllegalArgumentException("진행중인 프로모션이 아닙니다.");
    }

    // 3. 할인 금액 확인
    BigDecimal expected = BigDecimal.valueOf(promotion.discountPrice());
    if (expected.compareTo(paymentDetail.amount()) != 0) {
      throw new IllegalArgumentException("프로모션 할인 금액이 일치하지 않습니다.");
    }
  }
}

