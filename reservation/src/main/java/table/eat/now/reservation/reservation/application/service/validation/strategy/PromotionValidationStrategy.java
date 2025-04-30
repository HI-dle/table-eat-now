/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.service.validation.strategy;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import table.eat.now.common.exception.CustomException;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo.Promotion;
import table.eat.now.reservation.reservation.application.exception.ReservationErrorCode;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail.PaymentType;
import table.eat.now.reservation.reservation.application.service.validation.context.PromotionValidationContext;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class PromotionValidationStrategy extends
    AbstractContextAwarePaymentDetailValidationStrategy<PromotionValidationContext> {

  @Override
  public boolean supports(PaymentDetail paymentDetail) {
    return paymentDetail.type() == PaymentType.PROMOTION_EVENT;
  }

  @Override
  public PromotionValidationContext createContext() {
    return PromotionValidationContext.builder()
        .totalPrice(context.totalPrice())
        .paymentDetail(context.paymentDetail())
        .reservationDate(context.reservationDate())
        .promotionsMap(context.promotionsMap())
        .build();
  }

  @Override
  public void validateContext(PromotionValidationContext context) {
    Promotion promotion = Optional.ofNullable(context.promotionsMap()
            .get(context.paymentDetail().detailReferenceId()))
        .orElseThrow(() -> CustomException.from(ReservationErrorCode.PROMOTION_NOT_FOUND));

    // 상태 확인
    validatePromotionStatus(promotion);

    // 할인 금액 확인
    validatePromotionEventDiscountPrice(context.paymentDetail(), promotion.discountPrice());
  }

  private static void validatePromotionStatus(Promotion promotion) {
    if (promotion.promotionStatus() != Promotion.PromotionStatus.RUNNING) {
      throw CustomException.from(ReservationErrorCode.PROMOTION_INVALID_RUNNING);
    }
  }

  private static void validatePromotionEventDiscountPrice(PaymentDetail paymentDetail, BigDecimal expected) {
    if (expected.compareTo(paymentDetail.amount()) != 0) {
      throw CustomException.from(ReservationErrorCode.INVALID_PROMOTION_DISCOUNT);
    }
  }
}

