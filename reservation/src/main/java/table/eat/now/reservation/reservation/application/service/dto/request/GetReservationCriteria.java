/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.reservation.application.service.dto.request;

import table.eat.now.common.resolver.dto.UserRole;

public record GetReservationCriteria(
    String reservationUuid,
    Long userId,
    UserRole role
) {
  public static GetReservationCriteria from(String uuid, UserRole role, Long userId) {
    return new GetReservationCriteria(uuid, userId, role);
  }
}
