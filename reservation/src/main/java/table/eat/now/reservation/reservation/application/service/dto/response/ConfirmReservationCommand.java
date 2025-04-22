package table.eat.now.reservation.reservation.application.service.dto.response;

import lombok.Builder;

@Builder
public record ConfirmReservationCommand(
    String idempotencyKey
) {
}
