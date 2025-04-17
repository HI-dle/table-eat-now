/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.application.service;

import java.time.LocalDateTime;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

public interface ReservationCancelPolicy {
  void validCancelableBy(
      Reservation reservation,
      LocalDateTime cancelRequestDateTime,
      Long requesterId,
      UserRole userRole
  );
}
