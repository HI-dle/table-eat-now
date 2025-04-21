package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record PreemptCouponRequest (
    String reservationId, List<String> userCouponUuids
){

  public static PreemptCouponRequest from(String reservationId, List<String> userCouponUuids) {
    return PreemptCouponRequest.builder()
        .reservationId(reservationId)
        .userCouponUuids(userCouponUuids)
        .build();
  }
}
