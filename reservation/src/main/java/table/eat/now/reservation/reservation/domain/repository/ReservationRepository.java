/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.reservation.reservation.domain.repository;

import java.util.List;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

public interface ReservationRepository {

  Reservation save(Reservation reservation);

  // test 용
  List<Reservation> findAll();
}
