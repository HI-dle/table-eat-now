package table.eat.now.payment.payment.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.payment.payment.application.client.ReservationClient;
import table.eat.now.payment.payment.application.client.dto.GetReservationInfo;
import table.eat.now.payment.payment.infrastructure.client.feign.ReservationFeignClient;

@Component
@RequiredArgsConstructor
public class ReservationClientImpl implements ReservationClient {

  private final ReservationFeignClient reservationFeignClient;

  @Override
  public GetReservationInfo getReservation(String reservationUuid) {
    return reservationFeignClient.getReservation(reservationUuid).toInfo();
  }
}
