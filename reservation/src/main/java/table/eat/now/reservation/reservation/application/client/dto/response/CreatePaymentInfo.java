/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.response;

public record CreatePaymentInfo(
    String paymentUuid,
    String idempotencyKey
) {}

