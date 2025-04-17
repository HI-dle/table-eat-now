/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 17.
 */
package table.eat.now.reservation.reservation.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelReservationRequest(
    @NotBlank
    @Size(max = 200, message = "취소이유(cancelReason)는 최대 200자까지 입력할 수 있습니다.")
    String cancelReason
) {

}
