package table.eat.now.payment.payment.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.payment.payment.infrastructure.client.config.FeignConfig;
import table.eat.now.payment.payment.infrastructure.client.dto.GetReservationResponse;

@FeignClient(name = "reservation", configuration = FeignConfig.class)
public interface ReservationFeignClient {

  @GetMapping("/api/v1/reservations/{reservationUuid}")
  GetReservationResponse getReservation(@PathVariable String reservationUuid);
}
