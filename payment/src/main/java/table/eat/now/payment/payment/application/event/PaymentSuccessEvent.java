package table.eat.now.payment.payment.application.event;


import static table.eat.now.payment.payment.application.event.EventType.SUCCEED;

import com.fasterxml.jackson.databind.JsonNode;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentSuccessEvent(
    EventType eventType,
    String paymentUuid,
    JsonNode payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static PaymentSuccessEvent of(
      PaymentSuccessPayload payload, CurrentUserInfoDto userInfo) {

    return new PaymentSuccessEvent(
        SUCCEED, payload.paymentUuid(), MapperUtil.toJsonNode(payload), userInfo);
  }
}
