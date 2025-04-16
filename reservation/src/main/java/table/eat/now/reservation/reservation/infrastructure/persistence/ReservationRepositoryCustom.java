/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.reservation.infrastructure.persistence;

import java.util.Optional;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

public interface ReservationRepositoryCustom {
  Optional<Reservation> findWithDetailsByReservationUuid(String reservationUuid);
}
