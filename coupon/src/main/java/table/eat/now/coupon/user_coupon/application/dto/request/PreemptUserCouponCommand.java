package table.eat.now.coupon.user_coupon.application.dto.request;

import java.util.Set;
import lombok.Builder;

@Builder
public record PreemptUserCouponCommand(
    String reservationUuid,
    Set<String> userCouponUuids
) {

}
