package table.eat.now.payment.payment.presentation.dto.response;

import java.math.BigDecimal;
import table.eat.now.payment.payment.application.dto.response.GetCheckoutDetailInfo;

public record GetCheckoutDetailResponse(
    String idempotencyKey,
    String customerKey,
    BigDecimal originalAmount,
    String reservationId,
    String reservationName) {

  public static GetCheckoutDetailResponse from(GetCheckoutDetailInfo info) {
    return new GetCheckoutDetailResponse(
        info.idempotencyKey(),
        info.customerKey(),
        info.originalAmount(),
        info.reservationId(),
        info.reservationName()
    );
  }

}
