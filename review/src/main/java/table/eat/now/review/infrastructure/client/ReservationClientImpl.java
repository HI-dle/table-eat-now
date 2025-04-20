package table.eat.now.review.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.client.dto.GetServiceInfo;
import table.eat.now.review.infrastructure.client.feign.ReservationFeignClient;

@Component
@RequiredArgsConstructor
public class ReservationClientImpl implements ReservationClient {

  private final ReservationFeignClient reservationFeignClient;

  @Override
  public GetServiceInfo getReservation(String reservationId) {
    return reservationFeignClient.getReservation(reservationId).toInfo();
  }
}
