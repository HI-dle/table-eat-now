/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long>,
    ReservationRepositoryCustom {

  Optional<Reservation> findByReservationUuid(String reservationUuid);
}
