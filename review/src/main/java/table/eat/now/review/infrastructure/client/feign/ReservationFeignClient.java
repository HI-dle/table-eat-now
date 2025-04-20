package table.eat.now.review.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import table.eat.now.review.infrastructure.client.config.FeignConfig;
import table.eat.now.review.infrastructure.client.dto.response.GetReservationResponse;

@FeignClient(name = "reservation", configuration = FeignConfig.class)
public interface ReservationFeignClient {

  @GetMapping("/internal/v1/reservations/{reservationUuid}")
  GetReservationResponse getReservation(@PathVariable String reservationUuid);


}
