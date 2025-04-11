package table.eat.now.payment.payment.presentation;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.payment.payment.presentation.dto.response.GetCheckoutDetailResponse;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  @GetMapping("/{idempotencyKey}/checkout-info")
  public ResponseEntity<GetCheckoutDetailResponse> getCheckoutDetail(
      @PathVariable UUID idempotencyKey) {
    GetCheckoutDetailResponse response = new GetCheckoutDetailResponse(
        idempotencyKey,
        1L,
        BigDecimal.valueOf(3000),
        "테스트 결제입니다.");
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{idempotencyKey}/confirm")
  public ResponseEntity<Void> confirmPayment(@PathVariable UUID idempotencyKey) {
    return ResponseEntity.ok().build();
  }
}
