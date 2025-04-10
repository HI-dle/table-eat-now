/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

import java.time.LocalDateTime;

public record CreatePaymentInfo(
    String paymentUuid,
    String idempotencyKey,
    String paymentStatus,
    int originalAmount,
    int discountAmount,
    int totalAmount,
    LocalDateTime createdAt
) {}

