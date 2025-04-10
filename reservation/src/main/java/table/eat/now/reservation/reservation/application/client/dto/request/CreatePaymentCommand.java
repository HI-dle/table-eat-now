/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.application.client.dto.request;

public record CreatePaymentCommand(
    String reservationUuid,
    String restaurantUuid,
    Long customerId,
    String reservationName,
    int originalAmount
) {

}
