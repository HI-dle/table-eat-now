/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.repository;

import java.util.List;
import java.util.Optional;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

public interface ReservationRepository {

  Reservation save(Reservation reservation);

  Optional<Reservation> findWithDetailsByReservationUuid(String reservationUuid);

  // test 용
  List<Reservation> findAll();

  <S extends Reservation> List<S> saveAll(Iterable<S> entities);
}
