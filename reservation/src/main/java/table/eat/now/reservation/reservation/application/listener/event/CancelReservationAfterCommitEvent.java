/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.listener.event;

import lombok.Builder;

@Builder
public record CancelReservationAfterCommitEvent(
    String reservationUuid
) {

  public static CancelReservationAfterCommitEvent from(String reservationUuid) {
    return CancelReservationAfterCommitEvent.builder()
        .reservationUuid(reservationUuid)
        .build(); // todo: 필요한 것들
  }
}
