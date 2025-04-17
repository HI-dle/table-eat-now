/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.service.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import table.eat.now.common.resolver.dto.UserRole;

@Builder
public record CancelReservationCommand(
    String reservationUuid,
    LocalDateTime cancelRequestDateTime,
    Long requesterId,
    UserRole userRole,
    String reason
) {

}
