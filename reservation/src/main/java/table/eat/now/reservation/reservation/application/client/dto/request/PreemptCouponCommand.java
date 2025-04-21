package table.eat.now.reservation.reservation.application.client.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record PreemptCouponCommand(String reservationId, List<String> userCouponUuids) {

  public static PreemptCouponCommand from(String reservationUuid, List<String> userCouponUuids) {
    return PreemptCouponCommand.builder()
        .reservationId(reservationUuid)
        .userCouponUuids(userCouponUuids)
        .build();
  }
}
