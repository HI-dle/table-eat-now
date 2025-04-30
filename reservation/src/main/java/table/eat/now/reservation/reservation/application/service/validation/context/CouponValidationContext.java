package table.eat.now.reservation.reservation.application.service.validation.context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import table.eat.now.reservation.reservation.application.client.dto.response.GetUserCouponsInfo.UserCoupon;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand.PaymentDetail;

@Builder
public record CouponValidationContext (
    Map<String, UserCoupon> couponMap,
    BigDecimal totalPrice,
    PaymentDetail paymentDetail,
    LocalDateTime reservationDate,
    Long reserverId
) implements ValidationPaymentDetailContext {}
