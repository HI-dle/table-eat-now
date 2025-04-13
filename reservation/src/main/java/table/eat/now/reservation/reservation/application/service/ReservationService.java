/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service;

import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;

public interface ReservationService {

  CreateReservationInfo createReservation(CreateReservationCommand command);
}
