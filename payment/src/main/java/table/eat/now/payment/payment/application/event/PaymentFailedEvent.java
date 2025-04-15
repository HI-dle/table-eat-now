package table.eat.now.payment.payment.application.event;

import static table.eat.now.payment.payment.application.event.EventType.FAILED;

import com.fasterxml.jackson.databind.JsonNode;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;

public record PaymentFailedEvent(
    EventType eventType,
    String paymentUuid,
    PaymentFailedPayload payload,
    CurrentUserInfoDto userInfo
) implements PaymentEvent {

  public static PaymentFailedEvent of(
      PaymentFailedPayload payload, CurrentUserInfoDto userInfo) {
    return new PaymentFailedEvent(
        FAILED, payload.paymentUuid(), payload, userInfo);
  }
}
