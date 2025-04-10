/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response;

import java.time.LocalDateTime;
import table.eat.now.reservation.reservation.application.client.dto.response.CreatePaymentInfo;

public record CreatePaymentResponse(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    int originalAmount,
    int discountAmount,
    int totalAmount,
    LocalDateTime createdAt
) {
  public CreatePaymentInfo toInfo() {
    return new CreatePaymentInfo(
        paymentUuid,
        idempotencyKey,
        paymentStatus,
        originalAmount,
        discountAmount,
        totalAmount,
        createdAt
    );
  }
}
