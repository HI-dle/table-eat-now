package table.eat.now.coupon.user_coupon.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;

@Builder
public record PreemptUserCouponRequest(
    @NotNull UUID reservationUuid
) {

  public PreemptUserCouponCommand toCommand() {
    return PreemptUserCouponCommand.builder()
        .reservationUuid(reservationUuid.toString())
        .build();
  }
}
