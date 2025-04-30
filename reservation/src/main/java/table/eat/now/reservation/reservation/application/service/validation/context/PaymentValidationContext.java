package table.eat.now.reservation.reservation.application.service.validation.context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@Builder
public record PaymentValidationContext(
    PaymentDetail paymentDetail,
    Map<String, UserCoupon> couponMap,
    Map<String, GetPromotionsInfo.Promotion> promotionsMap,
    BigDecimal totalPrice,
    LocalDateTime reservationDate,
    Long reserverId
) {}