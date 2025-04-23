package table.eat.now.reservation.reservation.application.service.dto.request;

import lombok.Builder;

@Builder
public record ConfirmReservationCommand(
    String idempotencyKey
) {
}
