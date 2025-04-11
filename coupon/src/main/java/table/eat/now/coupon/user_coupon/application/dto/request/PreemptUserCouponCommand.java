package table.eat.now.coupon.user_coupon.application.dto.request;

import lombok.Builder;

@Builder
public record PreemptUserCouponCommand(
    String reservationUuid
) {

}
