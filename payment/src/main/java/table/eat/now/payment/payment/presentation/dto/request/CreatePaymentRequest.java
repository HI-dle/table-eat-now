package table.eat.now.payment.payment.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import table.eat.now.payment.payment.application.dto.request.CreatePaymentCommand;

public record CreatePaymentRequest(
    @NotNull String reservationUuid,
    @NotNull String restaurantUuid,
    @NotNull Long customerId, // 꼭 필요할까요 (user context 에서 받을 수 있음)
    @NotNull String reservationName,
    @NotNull BigDecimal originalAmount
) {

  public CreatePaymentCommand toCommand() {
    return CreatePaymentCommand.builder()
        .reservationUuid(reservationUuid)
        .restaurantUuid(restaurantUuid)
        .customerId(customerId)
        .reservationName(reservationName)
        .originalAmount(originalAmount)
        .build();
  }

}
