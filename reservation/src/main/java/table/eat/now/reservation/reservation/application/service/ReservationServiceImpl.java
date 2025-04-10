/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.domain.entity.Reservation;
import table.eat.now.reservation.reservation.infrastructure.persistence.JpaReservationRepository;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final JpaReservationRepository reservationRepository;


  @Override
  public CreateReservationInfo createRestaurant(CreateReservationCommand command) {
    String paymentKey = "";
    Reservation saved = reservationRepository.save(command.toEntityWithPaymentKey(paymentKey));
    return CreateReservationInfo.of(saved.getReservationUuid(), paymentKey);
  }
}
