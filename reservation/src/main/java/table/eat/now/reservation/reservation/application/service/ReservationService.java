/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.service;

import table.eat.now.reservation.reservation.application.service.dto.request.CancelReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.ConfirmReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.CreateReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.application.service.dto.response.CancelReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetReservationInfo;

public interface ReservationService {

  CreateReservationInfo createReservation(CreateReservationCommand command);

  GetReservationInfo getReservation(GetReservationCriteria criteria);

  void confirmReservation(ConfirmReservationCommand command);

  CancelReservationInfo cancelReservation(CancelReservationCommand command);
}
