package table.eat.now.reservation.reservation.infrastructure.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.domain.repository.ReservationRepository;

@RequiredArgsConstructor
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

  private final JpaReservationRepository jpaReservationRepository;

  @Override
  public Reservation save(Reservation reservation) {
    return jpaReservationRepository.save(reservation);
  }

  @Override
  public List<Reservation> findAll() {
    return jpaReservationRepository.findAll();
  }
}
