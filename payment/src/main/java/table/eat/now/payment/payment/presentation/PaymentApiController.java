package table.eat.now.payment.payment.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import table.eat.now.payment.payment.application.PaymentService;
import table.eat.now.payment.payment.presentation.dto.request.ConfirmPaymentRequest;
import table.eat.now.payment.payment.presentation.dto.response.ConfirmPaymentResponse;
import table.eat.now.payment.payment.presentation.dto.response.GetCheckoutDetailResponse;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentApiController {

  private final PaymentService paymentService;

  @GetMapping("/checkout-info")
  public ResponseEntity<GetCheckoutDetailResponse> getCheckoutDetail(
      @RequestParam UUID idempotencyKey) {

    return ResponseEntity.ok(GetCheckoutDetailResponse.from(
            paymentService.getCheckoutDetail(idempotencyKey.toString())));
  }

  @PatchMapping("/confirm")
  public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
      @RequestParam UUID reservationUuid,
      @RequestBody @Valid ConfirmPaymentRequest request) {

    return ResponseEntity.ok(ConfirmPaymentResponse.from(
        paymentService.confirmPayment(
            request.toCommand(reservationUuid.toString()))));
  }

}

