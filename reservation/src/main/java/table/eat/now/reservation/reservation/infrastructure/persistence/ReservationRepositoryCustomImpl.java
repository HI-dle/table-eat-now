/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.reservation.infrastructure.persistence;

import static table.eat.now.reservation.reservation.domain.entity.QReservation.reservation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import table.eat.now.reservation.reservation.domain.entity.QReservationPaymentDetail;
import table.eat.now.reservation.reservation.domain.entity.Reservation;

@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Reservation> findWithDetailsByReservationUuid(String reservationUuid) {
    return Optional.ofNullable(
        queryFactory
        .selectFrom(reservation)
        .leftJoin(reservation.paymentDetails.values,
            QReservationPaymentDetail.reservationPaymentDetail).fetchJoin()
        .where(reservation.reservationUuid.eq(reservationUuid))
        .fetchOne());
  }
}
