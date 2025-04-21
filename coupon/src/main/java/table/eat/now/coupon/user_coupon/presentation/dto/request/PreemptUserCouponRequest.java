package table.eat.now.coupon.user_coupon.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import table.eat.now.coupon.user_coupon.application.dto.request.PreemptUserCouponCommand;

@Builder
public record PreemptUserCouponRequest(
    @NotNull UUID reservationUuid,
    @NotEmpty Set<UUID> userCouponUuids
) {

  public PreemptUserCouponCommand toCommand() {
    return PreemptUserCouponCommand.builder()
        .reservationUuid(reservationUuid.toString())
        .userCouponUuids(userCouponUuids.stream().map(UUID::toString).collect(Collectors.toSet()))
        .build();
  }
}
